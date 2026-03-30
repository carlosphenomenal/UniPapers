package com.unipapers.unipapers_frontend.core.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Browse : Screen("browse")
    object Downloads : Screen("downloads")
    object Profile : Screen("profile")
    
    // Add other screens as needed, e.g., PaperDetail
    object Login : Screen("login")
    object Register : Screen("register")
    object Upload : Screen("upload")
}
