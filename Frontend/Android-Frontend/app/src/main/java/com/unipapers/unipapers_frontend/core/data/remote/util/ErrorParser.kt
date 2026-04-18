package com.unipapers.unipapers_frontend.core.data.remote.util

import com.google.gson.Gson
import com.unipapers.unipapers_frontend.core.data.remote.dto.ErrorResponseDto
import retrofit2.Response

object ErrorParser {
    fun parseErrorMessage(response: Response<*>, gson: Gson): String {
        return try {
            val errorBody = response.errorBody()?.string()
            if (errorBody != null) {
                val errorResponse = gson.fromJson(errorBody, ErrorResponseDto::class.java)
                errorResponse.message
            } else {
                response.message().ifBlank { "An unknown error occurred" }
            }
        } catch (_: Exception) {
            response.message().ifBlank { "An unknown error occurred" }
        }
    }
}
