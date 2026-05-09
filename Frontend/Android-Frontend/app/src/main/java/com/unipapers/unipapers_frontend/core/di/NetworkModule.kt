package com.unipapers.unipapers_frontend.core.di

import com.google.gson.Gson
import com.unipapers.unipapers_frontend.feature.home.data.datasource.UniPapersApiService
import com.unipapers.unipapers_frontend.core.data.remote.interceptor.AuthInterceptor
import com.unipapers.unipapers_frontend.feature.filemanagement.data.datasource.CloudUploadApi
import com.unipapers.unipapers_frontend.feature.filemanagement.data.datasource.FileApi
import com.unipapers.unipapers_frontend.feature.profile.data.datasource.ProfileApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideAuthInterceptor(): AuthInterceptor = AuthInterceptor()

    // 1. Provide the Logging Interceptor
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    // 2. Add the logger to the OkHttpClient
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor) // Adds the logger here
            .build()
    }

    // 3. Provide Retrofit using the OkHttpClient from above
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideFileApi(retrofit: Retrofit): FileApi = retrofit.create(FileApi::class.java)

    @Provides
    @Singleton
    fun provideCloudUploadApi(retrofit: Retrofit): CloudUploadApi = retrofit.create(CloudUploadApi::class.java)

    @Provides
    @Singleton
    fun provideProfileApi(retrofit: Retrofit): ProfileApiService = retrofit.create(ProfileApiService::class.java)

    @Provides
    @Singleton
    fun provideUniPapersApiService(retrofit: Retrofit): UniPapersApiService = retrofit.create(UniPapersApiService::class.java)
}