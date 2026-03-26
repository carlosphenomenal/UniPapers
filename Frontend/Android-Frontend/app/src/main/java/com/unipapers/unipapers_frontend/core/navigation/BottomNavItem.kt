package com.unipapers.unipapers_frontend.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = Screen.Home.route,
        title = "Home",
        icon = Icons.Outlined.Home
    )

    object Browse : BottomNavItem(
        route = Screen.Browse.route,
        title = "Browse",
        icon = Icons.Outlined.GridView
    )

    object Downloads : BottomNavItem(
        route = Screen.Downloads.route,
        title = "Downloads",
        icon = Icons.Outlined.Download
    )

    object Profile : BottomNavItem(
        route = Screen.Profile.route,
        title = "Profile",
        icon = Icons.Outlined.AccountCircle
    )
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Browse,
    BottomNavItem.Downloads,
    BottomNavItem.Profile
)
