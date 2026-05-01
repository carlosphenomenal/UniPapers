package com.unipapers.unipapers_frontend.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.unipapers.unipapers_frontend.core.ui.components.ToastManager
import com.unipapers.unipapers_frontend.feature.auth.presentation.login.LoginScreen
import com.unipapers.unipapers_frontend.feature.auth.presentation.register.RegisterScreen
import com.unipapers.unipapers_frontend.feature.browse.presentation.BrowsePapersScreen
import com.unipapers.unipapers_frontend.feature.browse.presentation.CourseUnitScreen
import com.unipapers.unipapers_frontend.feature.filemanagement.presentation.screens.DownloadScreen
import com.unipapers.unipapers_frontend.feature.filemanagement.presentation.screens.UploadFlowScreen
import com.unipapers.unipapers_frontend.feature.home.presentation.screens.HomeScreen
import com.unipapers.unipapers_frontend.feature.pdfviewer.presentation.PdfViewerScreen
import com.unipapers.unipapers_frontend.feature.profile.presentation.ProfileScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun NavGraph(
    navController: NavHostController,
    toastManager: ToastManager,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,

        startDestination = Screen.Login.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Browse.route) {
            CourseUnitScreen(
                onCourseClick = { courseCode ->
                    navController.navigate(Screen.BrowsePapers.createRoute(courseCode))
                }
            )
        }
        composable(
            route = Screen.BrowsePapers.route,
            arguments = listOf(navArgument("courseCode") { type = NavType.StringType })
        ) {
            BrowsePapersScreen(
                onBackClick = { navController.popBackStack() },
                onPaperClick = { paperId ->
                    // For now, we'll navigate to PDF viewer directly if we had the URL.
                    // In a real app, you might fetch the paper details first to get the URL.
                },
                onNavigateToPdf = { url ->
                    navController.navigate(Screen.PdfViewer.createRoute(url))
                }
            )
        }
        composable(
            route = Screen.PdfViewer.route,
            arguments = listOf(navArgument("url") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedUrl = backStackEntry.arguments?.getString("url") ?: ""
            val url = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString())
            PdfViewerScreen(
                url = url,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.Downloads.route) {
            DownloadScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }
        composable(Screen.Upload.route) {
            UploadFlowScreen(
                onBackClick = { navController.popBackStack() },
                toastManager = toastManager
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToForgotPassword = {
                    // TODO: Implement forgot password
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
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
