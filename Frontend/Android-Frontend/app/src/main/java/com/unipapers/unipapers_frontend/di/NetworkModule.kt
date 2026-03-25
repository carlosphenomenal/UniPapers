package com.unipapers.unipapers_frontend.di

import com.unipapers.unipapers_frontend.data.remote.api.CloudUploadApi
import com.unipapers.unipapers_frontend.data.remote.api.FileApi
import com.unipapers.unipapers_frontend.data.repository.FileRepositoryImpl
import com.unipapers.unipapers_frontend.domain.repository.FileRepository
import dagger.Module
import dagger.Provides
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
    fun provideFileRepository(fileApi: FileApi, cloudUploadApi: CloudUploadApi): FileRepository {
        return FileRepositoryImpl(fileApi, cloudUploadApi)
    }
}