package com.unipapers.unipapers_frontend.core.di

import com.unipapers.unipapers_frontend.BuildConfig

object NetworkConfig {
    val BASE_URL: String by lazy {
        BuildConfig.BASE_URL
    }

    /**
     * Endpoints that do not require an Authorization header.
     * Add paths here (without the base URL) as you introduce protected routes.
     */
    val PUBLIC_ENDPOINTS: Set<String> = setOf(
        "auth/login",
        "auth/signup",
        "auth/verify-email",
        "auth/resend-verification-code",
        "auth/refresh",
        "programs/get"
    )
}
