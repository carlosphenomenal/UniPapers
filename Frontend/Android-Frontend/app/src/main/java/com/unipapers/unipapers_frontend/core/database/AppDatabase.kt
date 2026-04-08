package com.unipapers.unipapers_frontend.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.unipapers.unipapers_frontend.feature.filemanagement.data.local.DownloadDao
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.model.Download

@Database(entities = [Download::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao
}
