package com.unipapers.unipapers_frontend.core.data.remote.interceptor

import com.unipapers.unipapers_frontend.core.data.local.AppPreferences
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val prefs: AppPreferences,
    private val refreshCoordinator: TokenRefreshCoordinator
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            return null
        }

        if (!isExpiredTokenResponse(response)) {
            return null
        }

        val currentToken = prefs.getAccessToken()
        val requestToken = response.request.header("Authorization")
            ?.removePrefix("Bearer ")
            ?.trim()

        if (!currentToken.isNullOrBlank() && !requestToken.isNullOrBlank() && requestToken != currentToken) {
            return response.request.newBuilder()
                .header("Authorization", "Bearer $currentToken")
                .build()
        }

        val newToken = refreshCoordinator.refreshAccessTokenBlocking(requestToken ?: currentToken)
            ?: return null

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newToken")
            .build()
    }

    private fun isExpiredTokenResponse(response: Response): Boolean {
        val expiredHeader = response.header("X-Auth-Error")?.trim()?.lowercase()
        if (expiredHeader == "token_expired") {
            return true
        }

        val wwwAuthenticate = response.header("WWW-Authenticate")?.lowercase()
        if (wwwAuthenticate != null &&
            wwwAuthenticate.contains("invalid_token") &&
            wwwAuthenticate.contains("access token expired")
        ) {
            return true
        }

        return false
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            count += 1
            priorResponse = priorResponse.priorResponse
        }
        return count
    }
}
