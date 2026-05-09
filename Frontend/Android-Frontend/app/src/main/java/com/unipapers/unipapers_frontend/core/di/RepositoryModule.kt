package com.unipapers.unipapers_frontend.core.di

import android.content.Context
import com.google.gson.Gson
import com.unipapers.unipapers_frontend.core.data.local.AppPreferences
import com.unipapers.unipapers_frontend.core.data.remote.api.AuthApiService
import com.unipapers.unipapers_frontend.core.data.remote.api.ProgramApiService
import com.unipapers.unipapers_frontend.core.data.repository.CourseRepositoryImpl
import com.unipapers.unipapers_frontend.core.domain.repository.CourseRepository
import com.unipapers.unipapers_frontend.feature.auth.data.repository.AuthRepositoryImpl
import com.unipapers.unipapers_frontend.feature.auth.data.repository.ProgramRepositoryImpl
import com.unipapers.unipapers_frontend.feature.auth.domain.repository.AuthRepository
import com.unipapers.unipapers_frontend.feature.auth.domain.repository.ProgramRepository
import com.unipapers.unipapers_frontend.feature.browse.data.repository.BrowseRepositoryImpl
import com.unipapers.unipapers_frontend.feature.browse.domain.repository.BrowseRepository
import com.unipapers.unipapers_frontend.feature.filemanagement.data.datasource.CloudUploadApi
import com.unipapers.unipapers_frontend.feature.filemanagement.data.datasource.FileApi
import com.unipapers.unipapers_frontend.feature.filemanagement.data.local.DownloadDao
import com.unipapers.unipapers_frontend.feature.filemanagement.data.repository.FileRepositoryImpl
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.repository.FileRepository
import com.unipapers.unipapers_frontend.feature.home.data.datasource.UniPapersApiService
import com.unipapers.unipapers_frontend.feature.home.data.repository.HomeRepositoryImpl
import com.unipapers.unipapers_frontend.feature.home.domain.repository.HomeRepository
import com.unipapers.unipapers_frontend.feature.notifications.data.datasource.NotificationApiService
import com.unipapers.unipapers_frontend.feature.notifications.data.datasource.NotificationRemoteDataSource
import com.unipapers.unipapers_frontend.feature.notifications.data.repository.NotificationRepositoryImpl
import com.unipapers.unipapers_frontend.feature.notifications.domain.repository.NotificationRepository
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
        return ProfileRepositoryImpl(remoteDataSource, context, gson)
    }

    @Provides
    @Singleton
    fun provideHomeRepository(
        apiService: UniPapersApiService,
        fileApi: FileApi,
        gson: Gson
    ): HomeRepository {
        return HomeRepositoryImpl(apiService, fileApi, gson)
    }

    @Provides
    @Singleton
    fun provideCourseRepository(
        apiService: UniPapersApiService,
        gson: Gson
    ): CourseRepository {
        return CourseRepositoryImpl(apiService, gson)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthApiService,
        prefs: AppPreferences,
        gson: Gson
    ): AuthRepository {
        return AuthRepositoryImpl(api, prefs, gson)
    }

    @Provides
    @Singleton
    fun provideProgramRepository(
        api: ProgramApiService,
        gson: Gson
    ): ProgramRepository {
        return ProgramRepositoryImpl(api, gson)
    }

    @Provides
    @Singleton
    fun provideBrowseRepository(
        apiService: UniPapersApiService,
        fileApi: FileApi,
        gson: Gson
    ): BrowseRepository {
        return BrowseRepositoryImpl(apiService, fileApi, gson)
    }

    @Provides
    @Singleton
    fun provideNotificationRemoteDataSource(
        apiService: NotificationApiService
    ): NotificationRemoteDataSource {
        return NotificationRemoteDataSource(apiService)
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        remoteDataSource: NotificationRemoteDataSource,
        gson: Gson
    ): NotificationRepository {
        return NotificationRepositoryImpl(remoteDataSource, gson)
    }
}
