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
import com.unipapers.unipapers_frontend.feature.upload.domain.model.FileUploadDto
import com.unipapers.unipapers_frontend.feature.upload.domain.repository.FileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
    private val fileApi: FileApi,
    private val cloudUploadApi: CloudUploadApi,
    @param:ApplicationContext private val context: Context
) : FileRepository {
    companion object {
        val activeDownloadIds = mutableListOf<Long>()
    }
    override suspend fun uploadFile(fileUploadDto: FileUploadDto, file: File): Result<Unit> {
        return try {
            // Initialize upload
            val initResponse = fileApi.initializeUploadFile(fileUploadDto)
            if (!initResponse.isSuccessful) {
                return Result.failure(Exception("Failed to initialize upload: ${initResponse.message()}"))
            }

            val responseBody = initResponse.body()
                ?: return Result.failure(Exception("Response body is null"))

            val publicId = responseBody.publicId
            val signedUrl = responseBody.signedUrl

            // Upload file to signed URL
            val requestBody = file.asRequestBody("application/pdf".toMediaTypeOrNull())
            val uploadResponse = cloudUploadApi.uploadFile(
                url = signedUrl,
                contentType = "application/pdf",
                body = requestBody
            )

            if (!uploadResponse.isSuccessful) {
                return Result.failure(Exception("Failed to upload file to bucket: ${uploadResponse.message()}"))
            }

            // confirm upload
            val confirmResponse = fileApi.confirmUpload(publicId)
            if (!confirmResponse.isSuccessful) {
                return Result.failure(Exception("Failed to confirm upload: ${confirmResponse.message()}"))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override suspend fun downloadFile(pastPaperPublicId: String): Result<Unit> {
        var createdUri: Uri? = null
        var enqueuedDownloadId: Long? = null

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
            createdUri = createDownloadDestination(fileName)
                ?: return Result.failure(IOException("Failed to create destination in Downloads/UniPapers"))

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

            enqueuedDownloadId = downloadManager.enqueue(request)
            activeDownloadIds.add(enqueuedDownloadId)

            //TODO: Handle download progress
            //TODO: Handle download completion(broadcast receiver)

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

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createDownloadDestination(fileName: String): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/UniPapers")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        return context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)?.also { uri ->
            context.contentResolver.update(
                uri,
                ContentValues().apply { put(MediaStore.Downloads.IS_PENDING, 0) },
                null,
                null
            )
        }
    }

    private fun cleanupCancelledDownload(uri: Uri?, downloadId: Long?) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
        if (downloadId != null) {
            runCatching { downloadManager?.remove(downloadId) }
            activeDownloadIds.remove(downloadId)
        }
        if (uri != null) {
            runCatching { context.contentResolver.delete(uri, null, null) }
        }
    }

    private fun normalizeFileName(rawKey: String): String {
        val lastSegment = rawKey.substringAfterLast('/').trim()
        val baseName = lastSegment.ifBlank { "paper_${System.currentTimeMillis()}" }
        val withExtension = if (baseName.lowercase(Locale.US).endsWith(".pdf")) baseName else "$baseName.pdf"
        val safeName = withExtension.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        return "${System.currentTimeMillis()}_$safeName"
    }
}
