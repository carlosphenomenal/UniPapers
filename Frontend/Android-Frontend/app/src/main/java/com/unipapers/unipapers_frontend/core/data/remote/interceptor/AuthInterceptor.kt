package com.unipapers.unipapers_frontend.core.data.remote.interceptor

import com.unipapers.unipapers_frontend.core.data.local.AppPreferences
import com.unipapers.unipapers_frontend.core.di.NetworkConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val prefs: AppPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // normalize leading slash so the comparison is consistent
        val requestPath = originalRequest.url.encodedPath.trimStart('/')

        val isPublic = NetworkConfig.PUBLIC_ENDPOINTS.any { publicPath ->
            requestPath.startsWith(publicPath)
        }

        if (isPublic) {
            // Public endpoint, forward as-is
            return chain.proceed(originalRequest)
        }

        val token = prefs.getAccessToken()
            ?: return chain.proceed(originalRequest)

        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}
