package com.unipapers.unipapers_frontend.core.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.unipapers.unipapers_frontend.core.data.local.AppPreferences
import com.unipapers.unipapers_frontend.core.data.remote.api.AuthApiService
import com.unipapers.unipapers_frontend.core.data.remote.api.ProgramApiService
import com.unipapers.unipapers_frontend.core.data.remote.interceptor.AuthInterceptor
import com.unipapers.unipapers_frontend.core.data.remote.interceptor.TokenAuthenticator
import com.unipapers.unipapers_frontend.core.data.remote.interceptor.TokenRefreshCoordinator
import com.unipapers.unipapers_frontend.feature.filemanagement.data.datasource.CloudUploadApi
import com.unipapers.unipapers_frontend.feature.filemanagement.data.datasource.FileApi
import com.unipapers.unipapers_frontend.feature.home.data.datasource.UniPapersApiService
import com.unipapers.unipapers_frontend.feature.notifications.data.datasource.NotificationApiService
import com.unipapers.unipapers_frontend.feature.profile.data.datasource.ProfileApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Suppress("unused")
    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().setLenient().create()

    @Provides
    @Singleton
    fun provideAuthInterceptor(prefs: AppPreferences): AuthInterceptor = AuthInterceptor(prefs)

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        prefs: AppPreferences,
        refreshCoordinator: TokenRefreshCoordinator
    ): TokenAuthenticator = TokenAuthenticator(prefs, refreshCoordinator)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("refresh")
    fun provideRefreshOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @Provides
    @Singleton
    @Named("refresh")
    fun provideRefreshRetrofit(
        @Named("refresh") okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    @Named("refresh")
    fun provideRefreshAuthApiService(@Named("refresh") retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFileApi(retrofit: Retrofit): FileApi {
        return retrofit.create(FileApi::class.java)
    }

    @Provides
    @Singleton
    @Named("cloud")
    fun provideCloudUploadOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @Provides
    @Singleton
    @Named("cloud")
    fun provideCloudUploadRetrofit(
        @Named("cloud") okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideCloudUploadApi(@Named("cloud") retrofit: Retrofit): CloudUploadApi {
        return retrofit.create(CloudUploadApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileApi(retrofit: Retrofit): ProfileApiService {
        return retrofit.create(ProfileApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUniPapersApiService(retrofit: Retrofit): UniPapersApiService {
        return retrofit.create(UniPapersApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideProgramApiService(retrofit: Retrofit): ProgramApiService {
        return retrofit.create(ProgramApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationApiService(retrofit: Retrofit): NotificationApiService {
        return retrofit.create(NotificationApiService::class.java)
    }
}
