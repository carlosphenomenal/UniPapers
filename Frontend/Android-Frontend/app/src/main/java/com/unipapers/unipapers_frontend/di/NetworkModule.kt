package com.unipapers.unipapers_frontend.di

import com.unipapers.unipapers_frontend.data.remote.api.CloudUploadApi
import com.unipapers.unipapers_frontend.data.remote.api.FileApi
import com.unipapers.unipapers_frontend.data.repository.FileRepositoryImpl
import com.unipapers.unipapers_frontend.domain.repository.FileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://your-api-base-url.com/" // Replace with your actual base URL

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
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
    fun provideFileRepository(fileApi: FileApi, cloudUploadApi: CloudUploadApi): FileRepository {
        return FileRepositoryImpl(fileApi, cloudUploadApi)
    }
}
