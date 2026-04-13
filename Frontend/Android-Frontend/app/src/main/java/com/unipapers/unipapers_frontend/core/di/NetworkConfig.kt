package com.unipapers.unipapers_frontend.core.di

object NetworkConfig {
    const val BASE_URL = "http://192.168.43.11:8080/api/" // Replace the IP address with your machine's IP

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