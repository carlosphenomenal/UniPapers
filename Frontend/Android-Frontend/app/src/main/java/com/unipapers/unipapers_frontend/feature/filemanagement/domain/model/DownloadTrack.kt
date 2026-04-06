package com.unipapers.unipapers_frontend.feature.filemanagement.domain.model

data class DownloadTrack(
    val downloadId: Long,
    val pastPaperPublicId: String,
    val fileName: String,
    val destinationUri: String,
    val statusCode: Int,
    val progressPercent: Int,
    val downloadedBytes: Long,
    val totalBytes: Long,
    val reasonCode: Int?,
    val createdAtMillis: Long,
    val updatedAtMillis: Long
)

