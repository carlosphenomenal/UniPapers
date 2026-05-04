package com.unipapers.unipapers_frontend.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unipapers.unipapers_frontend.core.ui.theme.SimpleBlue
import com.unipapers.unipapers_frontend.feature.profile.presentation.components.ChangePasswordModal
import com.unipapers.unipapers_frontend.feature.profile.presentation.components.MyUploadsSection
import com.unipapers.unipapers_frontend.feature.profile.presentation.components.ProfileStatsRow
import com.unipapers.unipapers_frontend.feature.profile.presentation.components.SettingsSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onClearSuccessMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = SimpleBlue
                    )
                }
                state.error != null -> {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                state.profile != null -> {
                    val profile = state.profile!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        // Header Section
                        Box(modifier = Modifier.fillMaxWidth()) {
                            // Blue background with rounded corners and overscroll fill
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .drawBehind {
                                        // Extend the blue color upwards for overscroll
                                        drawRect(
                                            color = SimpleBlue,
                                            topLeft = Offset(0f, -1000.dp.toPx()),
                                            size = Size(size.width, 1000.dp.toPx())
                                        )
                                    }
                                    .background(
                                        color = SimpleBlue,
                                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                                    )
                            )
                            
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .statusBarsPadding()
                                    .padding(top = 24.dp)
                            ) {
                                Text(
                                    text = "Profile",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Profile Info Card
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        // Initials Avatar
                                        val initials = profile.fullName.split(" ")
                                            .filter { it.isNotBlank() }
                                            .mapNotNull { it.firstOrNull() }
                                            .joinToString("")
                                            .take(2)
                                            .uppercase()

                                        Surface(
                                            shape = CircleShape,
                                            color = Color(0xFFEAF5FF),
                                            modifier = Modifier.size(80.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = initials,
                                                    color = SimpleBlue,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 24.sp
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Text(
                                            text = profile.fullName,
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1A1C1E),
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = profile.email,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Color(0xFF5E6368),
                                            textAlign = TextAlign.Center
                                        )
                                        
                                        Spacer(modifier = Modifier.height(12.dp))
                                        
                                        Text(
                                            text = profile.programme,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF1A1C1E),
                                            textAlign = TextAlign.Center
                                        )
                                        
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        Text(
                                            text = "${profile.studentNumber}  •  Year ${profile.yearOfStudy}  •  Semester ${profile.currentSemester}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF5E6368),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        ProfileStatsRow(
                            uploadCount = profile.uploadCount,
                            downloadCount = profile.downloadCount
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        MyUploadsSection(
                            uploadCount = profile.uploadCount,
                            onUploadClick = { /* Handle navigate to upload */ }
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        SettingsSection(
                            onChangePassword = { viewModel.onShowChangePasswordModal() },
                            onUpdateYearSemester = { viewModel.onShowYearSemesterSheet() },
                            onLogout = { showLogoutDialog = true }
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "UniPapers v1.0 • Made for students",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFF5E6368),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 32.dp)
                        )
                    }
                }
            }
        }
    }

    if (state.showChangePasswordModal) {
        ChangePasswordModal(
            isLoading = state.isChangingPassword,
            errorMessage = state.passwordChangeError,
            onDismiss = { viewModel.onDismissChangePasswordModal() },
            onConfirm = { current, new -> viewModel.onChangePassword(current, new) }
        )
    }

    if (state.showYearSemesterSheet) {
        val sheetState = rememberModalBottomSheetState()
        var selectedYear by remember { mutableIntStateOf(state.profile?.yearOfStudy ?: 1) }
        var selectedSemester by remember { mutableIntStateOf(state.profile?.currentSemester ?: 1) }

        ModalBottomSheet(
            onDismissRequest = { viewModel.onDismissYearSemesterSheet() },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Update Year / Semester",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E)
                )
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Year of Study",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..4).forEach { year ->
                        FilterChip(
                            selected = selectedYear == year,
                            onClick = { selectedYear = year },
                            label = { Text("Year $year") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SimpleBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Semester",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..2).forEach { sem ->
                        FilterChip(
                            selected = selectedSemester == sem,
                            onClick = { selectedSemester = sem },
                            label = { Text("Semester $sem") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SimpleBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.onUpdateYearSemester(selectedYear, selectedSemester) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !state.isUpdatingProfile,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SimpleBlue
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (state.isUpdatingProfile) "Updating..." else "Update",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    viewModel.onLogout { onNavigateToLogin() }
                }) {
                    Text("Log Out", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = Color(0xFF5E6368))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }
}
