package com.unipapers.unipapers_frontend.feature.filemanagement.data.repository

import android.app.DownloadManager
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.unipapers.unipapers_frontend.feature.filemanagement.data.datasource.CloudUploadApi
import com.unipapers.unipapers_frontend.feature.filemanagement.data.datasource.FileApi
import com.unipapers.unipapers_frontend.feature.filemanagement.data.local.DownloadDao
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.model.Download
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.model.DownloadStatus
import com.unipapers.unipapers_frontend.feature.filemanagement.data.model.FileUploadDto
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.repository.FileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
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
    private val downloadDao: DownloadDao,
    @param:ApplicationContext private val context: Context
) : FileRepository {
    companion object {
        /** Flag to prevent multiple concurrent polling coroutines. */
        private val isPolling = AtomicBoolean(false)
    }
    private val _downloads = MutableStateFlow<List<Download>>(emptyList())
    override val downloads: StateFlow<List<Download>> = _downloads.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            downloadDao.observeAllDownloads().collect { downloads ->
                _downloads.value = downloads
            }
        }
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
                val errorBody = uploadResponse.errorBody()?.string()
                val detail = if (!errorBody.isNullOrBlank()) " - $errorBody" else ""
                return Result.failure(Exception("Failed to upload file to bucket: ${uploadResponse.code()}$detail"))
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
        return try {
            val response = fileApi.getPresignedDownloadUrl(pastPaperPublicId)
            if (!response.isSuccessful) {
                return Result.failure(Exception("Failed to get presigned download URL: ${response.message()}"))
            }

            val responseBody = response.body()
                ?: return Result.failure(Exception("Response body is null"))

            val signedUrl = responseBody.signedUrl
            val key = responseBody.key

            if (signedUrl.isBlank()) {
                return Result.failure(IllegalArgumentException("Signed URL is empty"))
            }

            val fileName = normalizeFileName(key)

            val request = DownloadManager.Request(signedUrl.toUri()).apply {
                setTitle(fileName)
                setDescription("Downloading $fileName")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setAllowedOverMetered(true)
                setAllowedOverRoaming(true)
                setMimeType("application/pdf")
                // I'm using this instead of setDestinationUri + MediaStore
                setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "UniPapers/$fileName"
                )
            }

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
                ?: return Result.failure(IllegalStateException("DownloadManager service is unavailable"))

            val enqueuedDownloadId = downloadManager.enqueue(request)

            val now = System.currentTimeMillis()
            upsertTrackedDownload(
                Download(
                    downloadId = enqueuedDownloadId,
                    pastPaperPublicId = pastPaperPublicId,
                    fileName = fileName,
                    destinationUri = "${Environment.DIRECTORY_DOWNLOADS}/UniPapers/$fileName",
                    status = DownloadStatus.PENDING,
                    statusCode = DownloadManager.STATUS_PENDING,
                    progressPercent = 0,
                    downloadedBytes = 0L,
                    totalBytes = -1L,
                    reasonCode = null,
                    createdAtMillis = now,
                    updatedAtMillis = now
                )
            )

            refreshTrackedDownload(enqueuedDownloadId)
            ensurePolling()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * This function is called by the broadcast receiver when there are completed or failed downloads from [DownloadManager].
     *
     * @param downloadId The ID assigned by [DownloadManager].
     */
    override fun syncDownloadedFile(downloadId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            refreshTrackedDownload(downloadId)
        }
    }

    /**
     * Cancels a download through [DownloadManager] and removes it from the database.
     */
    override suspend fun cancelDownload(downloadId: Long): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
                    ?: throw IllegalStateException("DownloadManager service is unavailable")
                downloadManager.remove(downloadId)
                removeTrackedDownload(downloadId)
            }
        }
    }

    /**
     * Pauses a download through [DownloadManager] by modifying its status via [android.content.ContentResolver].
     *
     * Note: [DownloadManager] doesn't expose a public pause/resume API.
     * This uses internal constants and might not work on all Android versions.
     */
    override suspend fun pauseDownload(downloadId: Long): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val existing = downloadDao.getDownloadById(downloadId)
                    ?: throw IOException("Download not found in database (id=$downloadId)")

                val originalStatus = existing.status
                val updatedDownload = existing.copy(
                    status = DownloadStatus.PAUSED,
                    updatedAtMillis = System.currentTimeMillis()
                )
                downloadDao.upsertDownload(updatedDownload)

                val contentValues = ContentValues().apply {
                    // Hidden internal constants for DownloadManager control
                    put("control", 1) // 1 = CONTROL_PAUSED
                }
                val downloadUri = ContentUris.withAppendedId("content://downloads/my_downloads".toUri(), downloadId)
                val updated = context.contentResolver.update(downloadUri, contentValues, null, null)
                if (updated <= 0) {
                    // If DM update fails, rollback Room status to original
                    downloadDao.upsertDownload(existing.copy(
                        status = originalStatus,
                        updatedAtMillis = System.currentTimeMillis()
                    ))
                }
                refreshTrackedDownload(downloadId)
            }
        }
    }

    /**
     * Resumes a paused download through [DownloadManager] by modifying its status via [android.content.ContentResolver].
     */
    override suspend fun resumeDownload(downloadId: Long): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val existing = downloadDao.getDownloadById(downloadId)
                    ?: throw IOException("Download not found in database (id=$downloadId)")

                val originalStatus = existing.status
                val updatedDownload = existing.copy(
                    status = DownloadStatus.DOWNLOADING,
                    updatedAtMillis = System.currentTimeMillis()
                )
                downloadDao.upsertDownload(updatedDownload)

                val contentValues = ContentValues().apply {
                    // Hidden internal constants for DownloadManager control
                    put("control", 0) // 0 = CONTROL_RUN
                }
                val downloadUri = ContentUris.withAppendedId("content://downloads/my_downloads".toUri(), downloadId)
                val updated = context.contentResolver.update(downloadUri, contentValues, null, null)
                if (updated <= 0) {
                    // If the DM update fails, roll back Room status to the original
                    downloadDao.upsertDownload(existing.copy(
                        status = originalStatus,
                        updatedAtMillis = System.currentTimeMillis()
                    ))
                }
                refreshTrackedDownload(downloadId)
            }
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
        // The backend saves the file a key in the format:
        // past-papers/exams/bachelor-of-science-in-software-engineering/year-1/semester-2/introduction-to-programming/2024-2025/01KNT4W1AAA77PM0CWVQJJDGM6-testpdf
        val lastSegment = rawKey.substringAfterLast('/').trim()
        // The backend saves the file name with a unique identifier before it eg., 01KNT4W1AAA77PM0CWVQJJDGM6-testpdf
        // so this step removes the unique identifier
        val nameWithoutUniqueIdentifier = lastSegment.substringAfterLast('-').trim()
        val baseName = nameWithoutUniqueIdentifier.ifBlank { "paper_${System.currentTimeMillis()}" }
        val withExtension = if (baseName.lowercase(Locale.US).endsWith(".pdf")) baseName else "$baseName.pdf"
        // This replaces unwanted characters (characters that are not letters, numbers, ., -, _)
        // with underscores to prevent issues with file systems and DownloadManager.
        val safeName = withExtension.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        return safeName
    }

    /**
     * Adds or updates a [Download] in the database.
     */
    private fun upsertTrackedDownload(download: Download) {
        CoroutineScope(Dispatchers.IO).launch {
            downloadDao.upsertDownload(download)
        }
    }

    /**
     * Removes a download from tracking (e.g., when it fails or is deleted).
     */
    private fun removeTrackedDownload(downloadId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            downloadDao.removeDownload(downloadId)
        }
    }

    /**
     * Refreshes the status of all currently tracked downloads.
     */
    private fun refreshPersistedDownloadStates() {
        CoroutineScope(Dispatchers.IO).launch {
            downloadDao.getAllTrackedDownloads()
                .map { it.downloadId }
                .forEach { refreshTrackedDownload(it) } // awaited
        }
    }

    /**
     * Queries [DownloadManager] for the current status of a specific download
     * and updates the database.
     */
    private suspend fun refreshTrackedDownload(downloadId: Long) {
        withContext(Dispatchers.IO) {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager ?: return@withContext
            val query = DownloadManager.Query().setFilterById(downloadId)

            runCatching {
                downloadManager.query(query)?.use { cursor ->
                    if (!cursor.moveToFirst()) {
                        removeTrackedDownload(downloadId)
                        return@runCatching
                    }

                    val status = cursor.getIntByName(DownloadManager.COLUMN_STATUS) ?: return@runCatching
                    val downloadedBytes = cursor.getLongByName(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR) ?: 0L
                    val totalBytes = cursor.getLongByName(DownloadManager.COLUMN_TOTAL_SIZE_BYTES) ?: -1L
                    val reasonCode = cursor.getIntByName(DownloadManager.COLUMN_REASON)
                    val progressPercent = calculateProgressPercent(downloadedBytes, totalBytes, status)

                    CoroutineScope(Dispatchers.IO).launch {
                        val existing = downloadDao.getDownloadById(downloadId)
                        if (existing != null) {
                            // Map DownloadManager status to our DownloadStatus
                            val mappedStatus = when (status) {
                                DownloadManager.STATUS_PENDING -> DownloadStatus.PENDING
                                DownloadManager.STATUS_RUNNING -> DownloadStatus.DOWNLOADING
                                DownloadManager.STATUS_PAUSED -> {
                                    when (reasonCode) {
                                        DownloadManager.PAUSED_WAITING_FOR_NETWORK,
                                        DownloadManager.PAUSED_QUEUED_FOR_WIFI -> DownloadStatus.WAITING_FOR_NETWORK
                                        else -> DownloadStatus.PAUSED
                                    }
                                }
                                DownloadManager.STATUS_SUCCESSFUL -> DownloadStatus.COMPLETED
                                DownloadManager.STATUS_FAILED -> DownloadStatus.FAILED
                                else -> existing.status
                            }

                            // If user manually paused, respect that unless it finished or failed.
                            // We also treat WAITING_FOR_NETWORK as a transient auto-pause that shouldn't
                            // override a deliberate user PAUSE.
                            val finalStatus = if (existing.status == DownloadStatus.PAUSED &&
                                (mappedStatus == DownloadStatus.DOWNLOADING || mappedStatus == DownloadStatus.WAITING_FOR_NETWORK)) {
                                DownloadStatus.PAUSED
                            } else {
                                mappedStatus
                            }

                            val updated = existing.copy(
                                status = finalStatus,
                                statusCode = status,
                                progressPercent = progressPercent,
                                downloadedBytes = downloadedBytes,
                                totalBytes = totalBytes,
                                reasonCode = reasonCode,
                                updatedAtMillis = System.currentTimeMillis()
                            )
                            downloadDao.upsertDownload(updated)
                        }
                    }
                }
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
     * Starts a background coroutine to poll for download progress if not already running.
     * The loop continues as long as there are pending downloads in the database.
     */
    private fun ensurePolling() {
        if (!isPolling.compareAndSet(false, true)) return // already running

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                while (true) {
                    val pendingIds = downloadDao.getAllTrackedDownloads()
                        .filter {
                            it.status != DownloadStatus.COMPLETED &&
                                    it.status != DownloadStatus.FAILED &&
                                    it.status != DownloadStatus.CANCELLED
                        }
                        .map { it.downloadId }

                    if (pendingIds.isEmpty()) break

                    pendingIds.forEach { refreshTrackedDownload(it) }
                    delay(50L) // Poll every 0.05 seconds
                }
            } finally {
                isPolling.set(false)
            }
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
