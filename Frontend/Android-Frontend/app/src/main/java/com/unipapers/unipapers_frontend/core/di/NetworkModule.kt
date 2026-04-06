package com.unipapers.unipapers_frontend.core.di

import android.content.Context
import com.unipapers.unipapers_frontend.core.data.remote.interceptor.AuthInterceptor
import com.unipapers.unipapers_frontend.feature.filemanagement.data.datasource.CloudUploadApi
import com.unipapers.unipapers_frontend.feature.filemanagement.data.datasource.FileApi
import com.unipapers.unipapers_frontend.feature.filemanagement.data.repository.FileRepositoryImpl
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.repository.FileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(): AuthInterceptor = AuthInterceptor()

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideFileApi(retrofit: Retrofit): FileApi {
        return retrofit.create(FileApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCloudUploadApi(retrofit: Retrofit): CloudUploadApi {
        return retrofit.create(CloudUploadApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFileRepository(
        fileApi: FileApi,
        cloudUploadApi: CloudUploadApi,
        @ApplicationContext context: Context
    ): FileRepository {
        return FileRepositoryImpl(fileApi, cloudUploadApi, context)
    }
}