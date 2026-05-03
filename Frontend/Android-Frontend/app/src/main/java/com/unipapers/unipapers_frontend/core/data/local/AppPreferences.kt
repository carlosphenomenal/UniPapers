package com.unipapers.unipapers_frontend.core.data.local

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import androidx.core.content.edit

class AppPreferences @Inject constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("unipapers_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit {
            putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
        }
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    fun clearTokens() {
        prefs.edit {
            remove(KEY_ACCESS_TOKEN)
                .remove(KEY_REFRESH_TOKEN)
        }
    }

    fun saveUser(userId: String, email: String) {
        prefs.edit {
            putString(KEY_USER_ID, userId)
                .putString(KEY_USER_EMAIL, email)
        }
    }

    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
}
