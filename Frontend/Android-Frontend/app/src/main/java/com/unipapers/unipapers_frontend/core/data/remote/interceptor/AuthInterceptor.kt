package com.unipapers.unipapers_frontend.core.data.remote.interceptor

import com.unipapers.unipapers_frontend.core.di.NetworkConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Intercepts outgoing HTTP requests and conditionally attaches an Authorization header.
 *
 * Only requests whose path matches an entry in [NetworkConfig.PROTECTED_ENDPOINTS] will
 * have the header appended — all other requests are forwarded unchanged.
 *
 * Usage: add new protected paths to [NetworkConfig.PROTECTED_ENDPOINTS] as the backend
 * introduces them; no changes to this class will be needed.
 *
 * Token management: call [AuthInterceptor.setToken] when the user logs in, and
 * [AuthInterceptor.clearToken] when the user logs out.
 */
@Singleton
class AuthInterceptor @Inject constructor() : Interceptor {

    @Volatile
    private var token: String? = null

    fun setToken(newToken: String) {
        token = newToken
    }

    fun clearToken() {
        token = null
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestPath = originalRequest.url.encodedPath
            .trimStart('/') // normalize leading slash so the comparison is consistent

        val isProtected = NetworkConfig.PROTECTED_ENDPOINTS.any { protectedPath ->
            requestPath.startsWith(protectedPath)
        }

        if (!isProtected) {
            // Public endpoint, forward as-is
            return chain.proceed(originalRequest)
        }

        val currentToken = token
            ?: return chain.proceed(originalRequest) // No token yet, let the server reject it

        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $currentToken")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}