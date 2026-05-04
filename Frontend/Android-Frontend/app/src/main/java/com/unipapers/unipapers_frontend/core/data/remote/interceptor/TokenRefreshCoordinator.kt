package com.unipapers.unipapers_frontend.core.data.remote.interceptor

import com.unipapers.unipapers_frontend.core.data.local.AppPreferences
import com.unipapers.unipapers_frontend.core.data.remote.api.AuthApiService
import kotlinx.coroutines.runBlocking
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.concurrent.withLock

@Singleton
class TokenRefreshCoordinator @Inject constructor(
    private val prefs: AppPreferences,
    @Named("refresh") private val authApiService: AuthApiService
) {
    private val lock = ReentrantLock()
    private val refreshFinished = lock.newCondition()
    private var refreshing = false

    fun refreshAccessTokenBlocking(expiredAccessToken: String?): String? {
        val currentToken = prefs.getAccessToken()
        if (!currentToken.isNullOrBlank() && currentToken != expiredAccessToken) {
            return currentToken
        }

        lock.withLock {
            if (refreshing) {
                while (refreshing) {
                    refreshFinished.await()
                }
                return prefs.getAccessToken()
            }
            refreshing = true
        }

        val newToken = try {
            val tokenToRefresh = expiredAccessToken ?: currentToken
            if (tokenToRefresh.isNullOrBlank()) {
                null
            } else {
                val response = runBlocking { authApiService.refresh(tokenToRefresh) }
                if (response.isSuccessful) {
                    response.body()?.also { body ->
                        prefs.saveTokens(body.accessToken, body.refreshToken)
                    }?.accessToken
                } else {
                    if (response.code() == 401) {
                        prefs.clearTokens()
                    }
                    null
                }
            }
        } catch (_: Exception) {
            null
        } finally {
            lock.withLock {
                refreshing = false
                refreshFinished.signalAll()
            }
        }

        return newToken
    }
}

