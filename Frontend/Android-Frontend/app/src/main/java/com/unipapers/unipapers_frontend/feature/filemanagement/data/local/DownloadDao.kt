package com.unipapers.unipapers_frontend.feature.filemanagement.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.model.Download
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloads")
    suspend fun getAllTrackedDownloads(): List<Download>

    @Query("SELECT * FROM downloads")
    fun observeAllDownloads(): Flow<List<Download>>

    @Upsert
    suspend fun upsertDownload(download: Download)

    @Query("DELETE FROM downloads WHERE downloadId = :downloadId")
    suspend fun removeDownload(downloadId: Long)

    @Query("SELECT * FROM downloads WHERE downloadId = :downloadId")
    suspend fun getDownloadById(downloadId: Long): Download?
}
