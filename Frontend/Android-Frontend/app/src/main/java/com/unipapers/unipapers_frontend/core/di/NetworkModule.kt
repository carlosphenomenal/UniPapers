package com.unipapers.unipapers_frontend.core.di

import com.google.gson.Gson
import com.unipapers.unipapers_frontend.core.data.local.AppPreferences
import com.unipapers.unipapers_frontend.core.data.remote.api.AuthApiService
import com.unipapers.unipapers_frontend.core.data.remote.api.ProgramApiService
import com.unipapers.unipapers_frontend.core.data.remote.interceptor.AuthInterceptor
import com.unipapers.unipapers_frontend.feature.filemanagement.data.datasource.CloudUploadApi
import com.unipapers.unipapers_frontend.feature.filemanagement.data.datasource.FileApi
import com.unipapers.unipapers_frontend.feature.home.data.datasource.UniPapersApiService
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
import com.unipapers.unipapers_frontend.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /*
     * NOTE: SharedPreferences and AppPreferences providers have been removed from here
     * because they are already provided in AppPreferencesModule.
     */

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        appPreferences: AppPreferences
    ): AuthInterceptor {
        return AuthInterceptor(appPreferences)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // --- API Service Providers ---

    @Provides
    @Singleton
    fun provideAuthApi(
        retrofit: Retrofit
    ): AuthApiService = retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideProgramApi(
        retrofit: Retrofit
    ): ProgramApiService = retrofit.create(ProgramApiService::class.java)

    @Provides
    @Singleton
    fun provideFileApi(
        retrofit: Retrofit
    ): FileApi = retrofit.create(FileApi::class.java)

    @Provides
    @Singleton
    fun provideCloudUploadApi(
        retrofit: Retrofit
    ): CloudUploadApi = retrofit.create(CloudUploadApi::class.java)

    @Provides
    @Singleton
    fun provideProfileApi(
        retrofit: Retrofit
    ): ProfileApiService = retrofit.create(ProfileApiService::class.java)

    @Provides
    @Singleton
    fun provideUniPapersApiService(
        retrofit: Retrofit
    ): UniPapersApiService = retrofit.create(UniPapersApiService::class.java)
}