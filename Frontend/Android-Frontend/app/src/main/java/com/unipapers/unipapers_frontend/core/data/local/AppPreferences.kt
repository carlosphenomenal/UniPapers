package com.unipapers.unipapers_frontend.core.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject

class AppPreferences @Inject constructor(context: Context) {
    private val appContext: Context = context
    private val userPrefs: SharedPreferences = context.getSharedPreferences(USER_PREFS_NAME, Context.MODE_PRIVATE)
    private val legacyPrefs: SharedPreferences = context.getSharedPreferences(LEGACY_PREFS_NAME, Context.MODE_PRIVATE)
    private val authPrefs: SharedPreferences = context.getSharedPreferences(AUTH_PREFS_NAME, Context.MODE_PRIVATE)
    private val secureAuthPrefs: SharedPreferences by lazy { createSecureAuthPrefs() }

    init {
        migrateLegacyPreferences()
    }

    companion object {
        private const val USER_PREFS_NAME = "unipapers_user_prefs"
        private const val LEGACY_PREFS_NAME = "unipapers_prefs"
        private const val AUTH_PREFS_NAME = "unipapers_auth_prefs"
        private const val SECURE_AUTH_PREFS_NAME = "unipapers_secure_auth_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_DEVICE_ID = "device_id"
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        authPrefs.edit { putString(KEY_ACCESS_TOKEN, accessToken) }
        saveRefreshToken(refreshToken)
        legacyPrefs.edit {
            remove(KEY_ACCESS_TOKEN)
                .remove(KEY_REFRESH_TOKEN)
        }
    }

    fun getAccessToken(): String? {
        authPrefs.getString(KEY_ACCESS_TOKEN, null)?.let { return it }

        val legacyAccessToken = legacyPrefs.getString(KEY_ACCESS_TOKEN, null)
        if (!legacyAccessToken.isNullOrBlank()) {
            authPrefs.edit { putString(KEY_ACCESS_TOKEN, legacyAccessToken) }
            legacyPrefs.edit { remove(KEY_ACCESS_TOKEN) }
        }

        return legacyAccessToken
    }

    fun getRefreshToken(): String? {
        readSecureRefreshToken()?.let { return it }

        val legacyRefreshToken = legacyPrefs.getString(KEY_REFRESH_TOKEN, null)
        if (!legacyRefreshToken.isNullOrBlank()) {
            saveRefreshToken(legacyRefreshToken)
            legacyPrefs.edit { remove(KEY_REFRESH_TOKEN) }
        }

        return legacyRefreshToken
    }

    fun clearTokens() {
        authPrefs.edit { remove(KEY_ACCESS_TOKEN) }
        secureAuthPrefs.edit { remove(KEY_REFRESH_TOKEN) }
        legacyPrefs.edit {
            remove(KEY_ACCESS_TOKEN)
                .remove(KEY_REFRESH_TOKEN)
        }
    }

    fun saveUser(userId: String, email: String, deviceId: String? = null) {
        userPrefs.edit {
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_EMAIL, email)
            deviceId?.let { putString(KEY_DEVICE_ID, it) }
        }
        legacyPrefs.edit {
            remove(KEY_USER_ID)
                .remove(KEY_USER_EMAIL)
        }
    }

    fun getUserId(): String? = userPrefs.getString(KEY_USER_ID, null)
        ?: legacyPrefs.getString(KEY_USER_ID, null)?.also { migratedUserId ->
            userPrefs.edit { putString(KEY_USER_ID, migratedUserId) }
            legacyPrefs.edit { remove(KEY_USER_ID) }
        }

    fun getUserEmail(): String? = userPrefs.getString(KEY_USER_EMAIL, null)
        ?: legacyPrefs.getString(KEY_USER_EMAIL, null)?.also { migratedUserEmail ->
            userPrefs.edit { putString(KEY_USER_EMAIL, migratedUserEmail) }
            legacyPrefs.edit { remove(KEY_USER_EMAIL) }
        }

    fun getDeviceId(): String? = userPrefs.getString(KEY_DEVICE_ID, null)

    private fun saveRefreshToken(refreshToken: String) {
        runCatching {
            secureAuthPrefs.edit { putString(KEY_REFRESH_TOKEN, refreshToken) }
        }.onFailure {
            appContext.deleteSharedPreferences(SECURE_AUTH_PREFS_NAME)
            createSecureAuthPrefs().edit { putString(KEY_REFRESH_TOKEN, refreshToken) }
        }
    }

    private fun readSecureRefreshToken(): String? {
        return runCatching {
            secureAuthPrefs.getString(KEY_REFRESH_TOKEN, null)
        }.getOrElse {
            appContext.deleteSharedPreferences(SECURE_AUTH_PREFS_NAME)
            null
        }
    }

    private fun createSecureAuthPrefs(): SharedPreferences {
        return try {
            buildSecureAuthPrefs()
        } catch (_: Exception) {
            appContext.deleteSharedPreferences(SECURE_AUTH_PREFS_NAME)
            buildSecureAuthPrefs()
        }
    }

    private fun migrateLegacyPreferences() {
        legacyPrefs.getString(KEY_ACCESS_TOKEN, null)?.let { legacyAccessToken ->
            if (legacyAccessToken.isNotBlank()) {
                authPrefs.edit { putString(KEY_ACCESS_TOKEN, legacyAccessToken) }
            }
        }

        legacyPrefs.getString(KEY_REFRESH_TOKEN, null)?.let { legacyRefreshToken ->
            if (legacyRefreshToken.isNotBlank()) {
                saveRefreshToken(legacyRefreshToken)
            }
        }

        legacyPrefs.getString(KEY_USER_ID, null)?.let { legacyUserId ->
            if (legacyUserId.isNotBlank()) {
                userPrefs.edit { putString(KEY_USER_ID, legacyUserId) }
            }
        }

        legacyPrefs.getString(KEY_USER_EMAIL, null)?.let { legacyUserEmail ->
            if (legacyUserEmail.isNotBlank()) {
                userPrefs.edit { putString(KEY_USER_EMAIL, legacyUserEmail) }
            }
        }

        legacyPrefs.edit {
            remove(KEY_ACCESS_TOKEN)
                .remove(KEY_REFRESH_TOKEN)
                .remove(KEY_USER_ID)
                .remove(KEY_USER_EMAIL)
        }
    }

    private fun buildSecureAuthPrefs(): SharedPreferences {
        val masterKey = MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            appContext,
            SECURE_AUTH_PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}
