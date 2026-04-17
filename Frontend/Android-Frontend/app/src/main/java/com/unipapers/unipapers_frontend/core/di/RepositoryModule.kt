package com.unipapers.unipapers_frontend.core.di

import android.content.Context
import com.unipapers.unipapers_frontend.feature.home.data.datasource.UniPapersApiService
import com.unipapers.unipapers_frontend.feature.filemanagement.data.datasource.CloudUploadApi
import com.unipapers.unipapers_frontend.feature.filemanagement.data.datasource.FileApi
import com.unipapers.unipapers_frontend.feature.filemanagement.data.local.DownloadDao
import com.unipapers.unipapers_frontend.feature.filemanagement.data.repository.FileRepositoryImpl
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.repository.FileRepository
import com.unipapers.unipapers_frontend.feature.home.data.repository.HomeRepositoryImpl
import com.unipapers.unipapers_frontend.feature.home.domain.repository.HomeRepository
import com.unipapers.unipapers_frontend.feature.profile.data.datasource.ProfileApiService
import com.unipapers.unipapers_frontend.feature.profile.data.datasource.ProfileRemoteDataSource
import com.unipapers.unipapers_frontend.feature.profile.data.repository.ProfileRepositoryImpl
import com.unipapers.unipapers_frontend.feature.profile.domain.repository.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.google.gson.Gson

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

    @Provides
    @Singleton
    fun provideProfileRemoteDataSource(
        profileApiService: ProfileApiService
    ): ProfileRemoteDataSource {
        return ProfileRemoteDataSource(profileApiService)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        remoteDataSource: ProfileRemoteDataSource,
        @ApplicationContext context: Context,
        gson: Gson
    ): ProfileRepository {
        return ProfileRepositoryImpl(
            remoteDataSource,
            context,
            gson
        )
    }

    @Provides
    @Singleton
    fun provideHomeRepository(
        apiService: UniPapersApiService,
        fileApi: FileApi
    ): HomeRepository {
        return HomeRepositoryImpl(apiService = apiService, fileApi = fileApi)
    }
}
