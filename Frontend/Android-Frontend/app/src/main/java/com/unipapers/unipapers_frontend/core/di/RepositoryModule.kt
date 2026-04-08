package com.unipapers.unipapers_frontend.core.di

import android.content.Context
import com.unipapers.unipapers_frontend.feature.filemanagement.data.datasource.CloudUploadApi
import com.unipapers.unipapers_frontend.feature.filemanagement.data.datasource.FileApi
import com.unipapers.unipapers_frontend.feature.filemanagement.data.local.DownloadDao
import com.unipapers.unipapers_frontend.feature.filemanagement.data.repository.FileRepositoryImpl
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.repository.FileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideFileRepository(
        fileApi: FileApi,
        cloudUploadApi: CloudUploadApi,
        downloadDao: DownloadDao,
        @ApplicationContext context: Context
    ): FileRepository {
        return FileRepositoryImpl(fileApi, cloudUploadApi, downloadDao, context)
    }
}
