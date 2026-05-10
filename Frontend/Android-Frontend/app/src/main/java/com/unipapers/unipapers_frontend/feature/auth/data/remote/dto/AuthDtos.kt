package com.unipapers.unipapers_frontend.feature.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SignupRequestDto(
    val firstName: String,
    val lastName: String,
    val email: String,
    val studentNumber: Long,
    val password: String,
    val programmePublicId: String,
    val yearOfStudy: Int
)

data class LoginRequestDto(
    val identifier: String, // email or student number
    val password: String,
    val fcmToken: String? = null
)

data class LoginResponseDto(
    val publicId: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val accessToken: String,
    val refreshToken: String,
    val deviceId: String,
    @SerializedName("emailVerified")
    val isEmailVerified: Boolean,
    val roles: List<String>
)

data class VerifyEmailRequestDto(
    val email: String,
    val verificationCode: String
)

data class ProgramResponseDto(
    val publicId: String,
    val programCode: String,
    val programName: String,
    val durationYears: Int
)

data class SendCodeRequestDto(
    val identifier: String
)

data class SendCodeResponseDto(
    val email: String,
    val message: String
)

data class UpdatePasswordRequestDto(
    val email: String,
    val newPassword: String
)