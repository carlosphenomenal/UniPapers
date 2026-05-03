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
    object Verify : Screen("verify/{email}") {
        fun createRoute(email: String) = "verify/$email"
    }
    object Upload : Screen("upload")

    object PdfViewer : Screen("pdf_viewer/{url}") {
        fun createRoute(url: String): String {
            val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
            return "pdf_viewer/$encodedUrl"
        }
    }
}
