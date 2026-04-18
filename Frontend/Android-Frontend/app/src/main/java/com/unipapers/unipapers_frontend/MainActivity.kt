package com.unipapers.unipapers_frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.unipapers.unipapers_frontend.core.navigation.NavGraph
import com.unipapers.unipapers_frontend.core.navigation.Screen
import com.unipapers.unipapers_frontend.core.ui.components.BottomNavBar
import com.unipapers.unipapers_frontend.core.ui.components.ToastData
import com.unipapers.unipapers_frontend.core.ui.components.ToastManager
import com.unipapers.unipapers_frontend.core.ui.components.UniPapersToast
import com.unipapers.unipapers_frontend.core.ui.theme.SimpleBlue
import com.unipapers.unipapers_frontend.core.ui.theme.UniPapersTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var toastManager: ToastManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UniPapersTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                var toastData by remember { mutableStateOf<ToastData?>(null) }
                var isToastVisible by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    toastManager.toastEvents.collectLatest { data ->
                        toastData = data
                        isToastVisible = true
                    }
                }

                val showBottomBar = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.Browse.route,
                    Screen.Downloads.route,
                    Screen.Profile.route
                )

                Box(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            if (showBottomBar) {
                                BottomNavBar(navController = navController)
                            }
                        },
                        floatingActionButton = {
                            if (currentRoute == Screen.Home.route) {
                                FloatingActionButton(
                                    onClick = { navController.navigate(Screen.Upload.route) },
                                    containerColor = SimpleBlue,
                                    contentColor = Color.White
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FileUpload,
                                        contentDescription = "Upload Paper"
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavGraph(
                            navController = navController,
                            toastManager = toastManager,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }

                    toastData?.let { data ->
                        UniPapersToast(
                            message = data.message,
                            icon = data.icon,
                            backgroundColor = data.backgroundColor ?: MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = data.contentColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                            isVisible = isToastVisible,
                            onDismiss = { isToastVisible = false },
                            duration = data.duration
                        )
                    }
                }
            }
        }
    }
}
