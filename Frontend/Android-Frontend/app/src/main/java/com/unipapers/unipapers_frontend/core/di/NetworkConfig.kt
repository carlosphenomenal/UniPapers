package com.unipapers.unipapers_frontend.core.di

import com.unipapers.unipapers_frontend.BuildConfig

object NetworkConfig {
    val BASE_URL: String by lazy {
        BuildConfig::class.java.getField("BASE_URL").get(null) as String
    }

    /**
     * Endpoints that require an Authorization header.
     * Add paths here (without the base URL) as you introduce protected routes.
     *
     * Example:
     *   "users/profile",
     *   "papers/delete/",
     */
    val PROTECTED_ENDPOINTS: Set<String> = setOf(
        // e.g. "users/me"
    )
}