package com.unipapers.unipapers_frontend.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.unipapers.unipapers_frontend.core.ui.components.ToastManager
import com.unipapers.unipapers_frontend.feature.auth.presentation.login.LoginScreen
import com.unipapers.unipapers_frontend.feature.auth.presentation.register.RegisterScreen
import com.unipapers.unipapers_frontend.feature.auth.presentation.verify.VerifyScreen
import com.unipapers.unipapers_frontend.feature.browse.presentation.BrowsePapersScreen
import com.unipapers.unipapers_frontend.feature.browse.presentation.CourseUnitScreen
import com.unipapers.unipapers_frontend.feature.filemanagement.presentation.screens.DownloadScreen
import com.unipapers.unipapers_frontend.feature.filemanagement.presentation.screens.UploadFlowScreen
import com.unipapers.unipapers_frontend.feature.home.presentation.screens.HomeScreen
import com.unipapers.unipapers_frontend.feature.pdfviewer.presentation.PdfViewerScreen
import com.unipapers.unipapers_frontend.feature.profile.presentation.ProfileScreen
import com.unipapers.unipapers_frontend.feature.auth.presentation.startup.StartupScreen
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
        startDestination = Screen.Startup.route,
        modifier = modifier
    ) {
        composable(Screen.Startup.route) {
            StartupScreen(
                onAuthenticated = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Startup.route) { inclusive = true }
                    }
                },
                onUnauthenticated = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Startup.route) { inclusive = true }
                    }
                }
            )
        }
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
                },
                onNavigateToVerify = { email ->
                    navController.navigate(Screen.Verify.createRoute(email, autoResend = false))
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = { email ->
                    navController.navigate(Screen.Verify.createRoute(email))
                }
            )
        }
        composable(
            route = Screen.Verify.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("autoResend") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) {
            VerifyScreen(
                onVerifySuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
