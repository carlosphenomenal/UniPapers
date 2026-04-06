package com.unipapers.unipapers_frontend.feature.upload.data.repository

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.unipapers.unipapers_frontend.feature.upload.data.datasource.CloudUploadApi
import com.unipapers.unipapers_frontend.feature.upload.data.datasource.FileApi
import com.unipapers.unipapers_frontend.feature.upload.domain.model.DownloadTrack
import com.unipapers.unipapers_frontend.feature.upload.domain.model.FileUploadDto
import com.unipapers.unipapers_frontend.feature.upload.domain.repository.FileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Implementation of [FileRepository] that handles file uploads and downloads.
 *
 * This class coordinates multi-step uploads (initialization, cloud storage upload, and confirmation)
 * and manages file downloads using Android's [DownloadManager] with persistent tracking
 * and progress polling.
 *
 * @property fileApi API for file metadata and upload/download orchestration.
 * @property cloudUploadApi API for direct cloud storage interactions.
 * @property context Application context for accessing system services and storage.
 */
class FileRepositoryImpl @Inject constructor(
    private val fileApi: FileApi,
    private val cloudUploadApi: CloudUploadApi,
    @param:ApplicationContext private val context: Context
) : FileRepository {
    companion object {
        private const val DOWNLOAD_TRACKING_PREFS = "download_tracking_prefs"
        private const val ACTIVE_DOWNLOADS_KEY = "active_downloads"

        /** Flag to prevent multiple concurrent polling coroutines. */
        private val isPolling = AtomicBoolean(false)

        /** In-memory list of downloads currently being tracked by the app. */
        val activeDownloads = mutableListOf<DownloadTrack>()
    }

    init {
        restorePersistedDownloads()
        refreshPersistedDownloadStates()
    }

    /**
     * Uploads a file following a three-step process:
     * 1. Initialize: Get a public ID and a signed URL from the backend.
     * 2. Upload: Send the file bytes directly to the cloud storage using the signed URL.
     * 3. Confirm: Notify the backend that the upload is complete.
     *
     * @param fileUploadDto Metadata for the file being uploaded.
     * @param file The physical file to be uploaded.
     * @return [Result.success] if all steps complete, [Result.failure] otherwise.
     */
    override suspend fun uploadFile(fileUploadDto: FileUploadDto, file: File): Result<Unit> {
        return try {
            // Initialize upload on the backend
            val initResponse = fileApi.initializeUploadFile(fileUploadDto)
            if (!initResponse.isSuccessful) {
                return Result.failure(Exception("Failed to initialize upload: ${initResponse.message()}"))
            }

            val responseBody = initResponse.body()
                ?: return Result.failure(Exception("Response body is null"))

            val publicId = responseBody.publicId
            val signedUrl = responseBody.signedUrl

            // Upload the file directly to cloud storage using the signed URL got from the backend
            val requestBody = file.asRequestBody("application/pdf".toMediaTypeOrNull())
            val uploadResponse = cloudUploadApi.uploadFile(
                url = signedUrl,
                contentType = "application/pdf",
                body = requestBody
            )

            if (!uploadResponse.isSuccessful) {
                return Result.failure(Exception("Failed to upload file to bucket: ${uploadResponse.message()}"))
            }

            // Confirm that the upload was successful on the backend
            val confirmResponse = fileApi.confirmUpload(publicId)
            if (!confirmResponse.isSuccessful) {
                return Result.failure(Exception("Failed to confirm upload: ${confirmResponse.message()}"))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Initiates a file download using Android's [DownloadManager].
     *
     * This method:
     * 1. Fetches a signed download URL from the backend that will be used to download the files from the bucket.
     * 2. Prepares a destination URI in the public Downloads directory.
     * 3. Enqueues the request in [DownloadManager].
     * 4. Starts tracking the download status and progress.
     *
     * @param pastPaperPublicId Unique identifier of the paper to download.
     * @return [Result.success] if enqueued, [Result.failure] if any step fails.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    override suspend fun downloadFile(pastPaperPublicId: String): Result<Unit> {
        var createdUri: Uri? = null
        var enqueuedDownloadId: Long? = null

        return try {
            // Get the file key and the signed url that will be used to download the file from the backend
            val response = fileApi.getPresignedDownloadUrl(pastPaperPublicId)
            if (!response.isSuccessful) {
                return Result.failure(Exception("Failed to get presigned download URL: ${response.message()}"))
            }

            val responseBody = response.body()
                ?: return Result.failure(Exception("Response body is null"))

            // Extract the signed url and the key from the body
            val signedUrl = responseBody.signedUrl
            val key = responseBody.key

            if (signedUrl.isBlank()) {
                return Result.failure(IllegalArgumentException("Signed URL is empty"))
            }

            // Create a file name (normalized key)
            val fileName = normalizeFileName(key)
            //Create the uri where the download will be stored
            createdUri = createDownloadDestination(fileName)
                ?: return Result.failure(IOException("Failed to create destination in Downloads/UniPapers"))

            // Initialize the download request using Download manager
            val request = DownloadManager.Request(signedUrl.toUri()).apply {
                setTitle(fileName)
                setDescription("Downloading $fileName")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setAllowedOverMetered(true)
                setAllowedOverRoaming(true)
                setMimeType("application/pdf")
                setDestinationUri(createdUri)
            }

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
                ?: return Result.failure(IllegalStateException("DownloadManager service is unavailable"))

            // Get the download id of this download
            enqueuedDownloadId = downloadManager.enqueue(request)

            // Add the download to the active downloads list for tracking
            val now = System.currentTimeMillis()
            upsertTrackedDownload(
                DownloadTrack(
                    downloadId = enqueuedDownloadId,
                    pastPaperPublicId = pastPaperPublicId,
                    fileName = fileName,
                    destinationUri = createdUri.toString(),
                    statusCode = DownloadManager.STATUS_PENDING,
                    progressPercent = 0,
                    downloadedBytes = 0L,
                    totalBytes = -1L,
                    reasonCode = null,
                    createdAtMillis = now,
                    updatedAtMillis = now
                )
            )

            // Refresh the downloads to ensure that the newly added download is recognized
            refreshTrackedDownload(enqueuedDownloadId)

            // Ensure that the polling coroutine is running. If it's not, it will be started.
            ensurePolling()

            Result.success(Unit)
        } catch (e: CancellationException) {
            cleanupCancelledDownload(createdUri, enqueuedDownloadId)
            throw e
        } catch (e: IllegalArgumentException) {
            cleanupCancelledDownload(createdUri, enqueuedDownloadId)
            Result.failure(IllegalArgumentException("Invalid download URL or destination", e))
        } catch (e: SecurityException) {
            cleanupCancelledDownload(createdUri, enqueuedDownloadId)
            Result.failure(SecurityException("No permission to write to Downloads", e))
        } catch (e: Exception) {
            cleanupCancelledDownload(createdUri, enqueuedDownloadId)
            Result.failure(e)
        }
    }

    /**
     * This function is called by the broadcast receiver when there are completed or failed downloads from [DownloadManager].
     *
     * @param downloadId The ID assigned by [DownloadManager].
     */
    override fun syncDownloadedFile(downloadId: Long) {
        refreshTrackedDownload(downloadId)
    }

    /**
     * Creates a placeholder in [MediaStore] for the incoming download.
     * This allows us to pre-define the file name and sub-directory (Downloads/UniPapers).
     *
     * @param fileName Desired name for the file.
     * @return [Uri] of the created placeholder, or null if creation fails.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createDownloadDestination(fileName: String): Uri? {
        // Define the metadata of the file
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/UniPapers")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        return context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)?.also { uri ->
            // Set the download to pending
            context.contentResolver.update(
                uri,
                ContentValues().apply { put(MediaStore.Downloads.IS_PENDING, 0) },
                null,
                null
            )
        }
    }

    /**
     * Cleans up local state and partially downloaded files if the process is cancelled or fails.
     */
    private fun cleanupCancelledDownload(uri: Uri?, downloadId: Long?) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
        if (downloadId != null) {
            runCatching { downloadManager?.remove(downloadId) }
            removeTrackedDownload(downloadId)
        }
        if (uri != null) {
            runCatching { context.contentResolver.delete(uri, null, null) }
        }
    }

    /**
     * Generates a safe and unique file name from a raw cloud key.
     * Ensures the name ends with .pdf and prevents illegal character issues.
     */
    private fun normalizeFileName(rawKey: String): String {
        val lastSegment = rawKey.substringAfterLast('/').trim()
        val baseName = lastSegment.ifBlank { "paper_${System.currentTimeMillis()}" }
        val withExtension = if (baseName.lowercase(Locale.US).endsWith(".pdf")) baseName else "$baseName.pdf"
        val safeName = withExtension.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        return "${System.currentTimeMillis()}_$safeName"
    }

    /**
     * Adds or updates a [DownloadTrack] in the in-memory list and persists it.
     */
    private fun upsertTrackedDownload(download: DownloadTrack) {
        synchronized(activeDownloads) {
            val index = activeDownloads.indexOfFirst { it.downloadId == download.downloadId }
            if (index >= 0) {
                activeDownloads[index] = download
            } else {
                activeDownloads.add(download)
            }
        }
        persistActiveDownloads()
    }

    /**
     * Removes a download from tracking (e.g., when it fails or is deleted).
     */
    private fun removeTrackedDownload(downloadId: Long) {
        synchronized(activeDownloads) {
            activeDownloads.removeAll { it.downloadId == downloadId }
        }
        persistActiveDownloads()
    }

    /**
     * Refreshes the status of all currently tracked downloads.
     */
    private fun refreshPersistedDownloadStates() {
        val persistedIds = synchronized(activeDownloads) { activeDownloads.map { it.downloadId } }
        persistedIds.forEach { refreshTrackedDownload(it) }
    }

    /**
     * Queries [DownloadManager] for the current status of a specific download
     * and updates the local [activeDownloads] list.
     */
    private fun refreshTrackedDownload(downloadId: Long) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager ?: return
        val query = DownloadManager.Query().setFilterById(downloadId)

        runCatching {
            downloadManager.query(query)?.use { cursor ->
                if (!cursor.moveToFirst()) {
                    removeTrackedDownload(downloadId)
                    return
                }

                val status = cursor.getIntByName(DownloadManager.COLUMN_STATUS) ?: return
                val downloadedBytes = cursor.getLongByName(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR) ?: 0L
                val totalBytes = cursor.getLongByName(DownloadManager.COLUMN_TOTAL_SIZE_BYTES) ?: -1L
                val reasonCode = cursor.getIntByName(DownloadManager.COLUMN_REASON)
                val progressPercent = calculateProgressPercent(downloadedBytes, totalBytes, status)

                synchronized(activeDownloads) {
                    val index = activeDownloads.indexOfFirst { it.downloadId == downloadId }
                    if (index >= 0) {
                        val existing = activeDownloads[index]
                        activeDownloads[index] = existing.copy(
                            statusCode = status,
                            progressPercent = progressPercent,
                            downloadedBytes = downloadedBytes,
                            totalBytes = totalBytes,
                            reasonCode = reasonCode,
                            updatedAtMillis = System.currentTimeMillis()
                        )
                    }
                }
                persistActiveDownloads()
            }
        }
    }

    /**
     * Calculates the progress percentage (0-100) based on bytes downloaded.
     */
    private fun calculateProgressPercent(downloadedBytes: Long, totalBytes: Long, statusCode: Int): Int {
        if (statusCode == DownloadManager.STATUS_SUCCESSFUL) return 100
        if (totalBytes <= 0L || downloadedBytes <= 0L) return 0
        return ((downloadedBytes * 100) / totalBytes).toInt().coerceIn(0, 100)
    }

    /**
     * Restores tracked downloads from [android.content.SharedPreferences] into memory.
     */
    private fun restorePersistedDownloads() {
        val prefs = context.getSharedPreferences(DOWNLOAD_TRACKING_PREFS, Context.MODE_PRIVATE)
        val json = prefs.getString(ACTIVE_DOWNLOADS_KEY, null) ?: return

        runCatching {
            val parsed = JSONArray(json)
            val restored = buildList {
                for (i in 0 until parsed.length()) {
                    val item = parsed.optJSONObject(i) ?: continue
                    downloadTrackFromJson(item)?.let { add(it) }
                }
            }

            synchronized(activeDownloads) {
                activeDownloads.clear()
                activeDownloads.addAll(restored)
            }
        }
    }

    /**
     * Saves the current list of tracked downloads to [android.content.SharedPreferences].
     */
    private fun persistActiveDownloads() {
        val prefs = context.getSharedPreferences(DOWNLOAD_TRACKING_PREFS, Context.MODE_PRIVATE)
        val serialized = JSONArray().apply {
            synchronized(activeDownloads) {
                activeDownloads.forEach { put(it.toJson()) }
            }
        }
        prefs.edit { putString(ACTIVE_DOWNLOADS_KEY, serialized.toString()) }
    }

    /**
     * Starts a background coroutine to poll for download progress if not already running.
     * The loop continues as long as there are pending downloads.
     */
    private fun ensurePolling() {
        if (!isPolling.compareAndSet(false, true)) return // already running

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                while (true) {
                    val pendingIds = synchronized(activeDownloads) {
                        activeDownloads
                            .filter {
                                it.statusCode != DownloadManager.STATUS_SUCCESSFUL &&
                                        it.statusCode != DownloadManager.STATUS_FAILED
                            }
                            .map { it.downloadId }
                    }

                    if (pendingIds.isEmpty()) break

                    pendingIds.forEach { refreshTrackedDownload(it) }
                    delay(1_500L) // Poll every 1.5 seconds
                }
            } finally {
                isPolling.set(false)
            }
        }
    }

    /**
     * Reconstructs a [DownloadTrack] from its JSON representation.
     */
    private fun downloadTrackFromJson(json: JSONObject): DownloadTrack? {
        val downloadId = json.optLong("downloadId", -1L)
        if (downloadId <= 0L) return null

        return DownloadTrack(
            downloadId = downloadId,
            pastPaperPublicId = json.optString("pastPaperPublicId", ""),
            fileName = json.optString("fileName", ""),
            destinationUri = json.optString("destinationUri", ""),
            statusCode = json.optInt("statusCode", DownloadManager.STATUS_PENDING),
            progressPercent = json.optInt("progressPercent", 0).coerceIn(0, 100),
            downloadedBytes = json.optLong("downloadedBytes", 0L),
            totalBytes = json.optLong("totalBytes", -1L),
            reasonCode = if (json.has("reasonCode") && !json.isNull("reasonCode")) json.optInt("reasonCode") else null,
            createdAtMillis = json.optLong("createdAtMillis", System.currentTimeMillis()),
            updatedAtMillis = json.optLong("updatedAtMillis", System.currentTimeMillis())
        )
    }

    /**
     * Serializes a [DownloadTrack] to a [JSONObject] for persistence.
     */
    private fun DownloadTrack.toJson(): JSONObject {
        return JSONObject().apply {
            put("downloadId", downloadId)
            put("pastPaperPublicId", pastPaperPublicId)
            put("fileName", fileName)
            put("destinationUri", destinationUri)
            put("statusCode", statusCode)
            put("progressPercent", progressPercent)
            put("downloadedBytes", downloadedBytes)
            put("totalBytes", totalBytes)
            put("reasonCode", reasonCode)
            put("createdAtMillis", createdAtMillis)
            put("updatedAtMillis", updatedAtMillis)
        }
    }

    /**
     * Extension function to safely retrieve an [Int] from a cursor by column name.
     */
    private fun android.database.Cursor.getIntByName(columnName: String): Int? {
        val index = getColumnIndex(columnName)
        return if (index >= 0) getInt(index) else null
    }

    /**
     * Extension function to safely retrieve a [Long] from a cursor by column name.
     */
    private fun android.database.Cursor.getLongByName(columnName: String): Long? {
        val index = getColumnIndex(columnName)
        return if (index >= 0) getLong(index) else null
    }
}
