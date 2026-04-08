package com.unipapers.unipapers_frontend.core.di

import android.content.Context
import androidx.room.Room
import com.unipapers.unipapers_frontend.core.database.AppDatabase
import com.unipapers.unipapers_frontend.feature.filemanagement.data.local.DownloadDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "unipapers_database"
        ).build()
    }

    @Provides
    fun provideDownloadDao(database: AppDatabase): DownloadDao {
        return database.downloadDao()
    }
}
