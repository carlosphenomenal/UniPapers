package com.unipapers.unipapers_frontend.feature.profile.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    // Collects the state from the ViewModel safely
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 1. Loading State
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // 2. Success State (Data loaded)
        state.user?.let { user ->
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header or Avatar could go here
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = user.fullName,
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Your custom component for stats
                ProfileStatsRow(user = user)
            }
        }

        // 3. Error State
        state.error?.let { message ->
            Text(
                text = "Error: $message",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

/**
 * A mock version for the Preview tab in Android Studio
 */
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    // This allows you to see the UI layout instantly on the right sidebar
    ProfileScreen()
}