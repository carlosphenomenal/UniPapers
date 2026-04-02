package com.unipapers.unipapers_frontend.core.di

import com.unipapers.unipapers_frontend.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GeminiModule {

    @Provides
    @Singleton
    @Named("gemini_api_key")
    fun provideGeminiApiKey(): String {
        return BuildConfig.GEMINI_API_KEY
    }
}
