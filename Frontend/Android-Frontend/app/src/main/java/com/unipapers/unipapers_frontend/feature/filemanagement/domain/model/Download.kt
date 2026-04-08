package com.unipapers.unipapers_frontend.feature.filemanagement.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class Download(
    @PrimaryKey val downloadId: Long,
    val pastPaperPublicId: String,
    val fileName: String,
    val destinationUri: String,
    val status: DownloadStatus,
    val statusCode: Int,
    val progressPercent: Int,
    val downloadedBytes: Long,
    val totalBytes: Long,
    val reasonCode: Int?,
    val createdAtMillis: Long,
    val updatedAtMillis: Long
)

