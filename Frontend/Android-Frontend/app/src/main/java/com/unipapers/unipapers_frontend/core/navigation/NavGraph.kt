package com.unipapers.unipapers_frontend.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.unipapers.unipapers_frontend.core.ui.components.ToastManager
import com.unipapers.unipapers_frontend.feature.filemanagement.presentation.screens.UploadFlowScreen
import com.unipapers.unipapers_frontend.feature.home.presentation.screens.HomeScreen
import com.unipapers.unipapers_frontend.feature.profile.presentation.ProfileScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    toastManager: ToastManager,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Browse.route) {
            PlaceholderScreen("Browse Screen")
        }
        composable(Screen.Downloads.route) {
            PlaceholderScreen("Downloads Screen")
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Upload.route) {
            UploadFlowScreen(
                onBackClick = { navController.popBackStack() },
                toastManager = toastManager
            )
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = name)
    }
}