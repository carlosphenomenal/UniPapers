package com.unipapers.unipapers_frontend.core.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Browse : Screen("browse")
    object BrowsePapers : Screen("browse_papers/{courseCode}") {
        fun createRoute(courseCode: String) = "browse_papers/$courseCode"
    }
    object Downloads : Screen("downloads")
    object Profile : Screen("profile")
    
    object Login : Screen("login")
    object Register : Screen("register")
    object Verify : Screen("verify/{email}?autoResend={autoResend}") {
        fun createRoute(email: String, autoResend: Boolean = false) =
            "verify/$email?autoResend=$autoResend"
    }
    object ForgotPasswordEmail : Screen("forgot_password_email")
    object ForgotPasswordVerify : Screen("forgot_password_verify/{email}?autoSend={autoSend}&fromProfile={fromProfile}") {
        fun createRoute(email: String, autoSend: Boolean = false, fromProfile: Boolean = false) =
            "forgot_password_verify/$email?autoSend=$autoSend&fromProfile=$fromProfile"
    }
    object ForgotPasswordReset : Screen("forgot_password_reset/{email}?fromProfile={fromProfile}") {
        fun createRoute(email: String, fromProfile: Boolean = false) =
            "forgot_password_reset/$email?fromProfile=$fromProfile"
    }
    object Upload : Screen("upload")

    object PdfViewer : Screen("pdf_viewer/{url}") {
        fun createRoute(url: String): String {
            val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
            return "pdf_viewer/$encodedUrl"
        }
    }
    object Startup : Screen("startup")
}
