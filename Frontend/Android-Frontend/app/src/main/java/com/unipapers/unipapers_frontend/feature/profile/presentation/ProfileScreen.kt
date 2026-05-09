package com.unipapers.unipapers_frontend.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unipapers.unipapers_frontend.core.ui.theme.SimpleBlue
import com.unipapers.unipapers_frontend.feature.profile.presentation.components.MyUploadsSection
import com.unipapers.unipapers_frontend.feature.profile.presentation.components.ProfileStatsRow
import com.unipapers.unipapers_frontend.feature.profile.presentation.components.SettingsSection
import com.unipapers.unipapers_frontend.feature.profile.domain.model.ProfileResponse
import com.unipapers.unipapers_frontend.feature.profile.domain.model.UserInfo
import com.unipapers.unipapers_frontend.feature.profile.domain.model.ProfileStats

@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToUpload: () -> Unit,
    onNavigateToForgotPassword: (String) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Pass everything to the Stateless Content Composable
    ProfileContent(
        state = state,
        onNavigateToLogin = onNavigateToLogin,
        onNavigateToUpload = onNavigateToUpload,
        onNavigateToForgotPassword = onNavigateToForgotPassword,
        onClearSuccessMessage = { viewModel.onClearSuccessMessage() },
        onShowYearSemesterSheet = { viewModel.onShowYearSemesterSheet() },
        onDismissYearSemesterSheet = { viewModel.onDismissYearSemesterSheet() },
        onUpdateYearSemester = { year, sem -> viewModel.onUpdateYearSemester(year, sem) },
        onLogoutConfirm = { viewModel.onLogout { onNavigateToLogin() } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    state: ProfileState,
    onNavigateToLogin: () -> Unit,
    onNavigateToUpload: () -> Unit,
    onNavigateToForgotPassword: (String) -> Unit,
    onClearSuccessMessage: () -> Unit,
    onShowYearSemesterSheet: () -> Unit,
    onDismissYearSemesterSheet: () -> Unit,
    onUpdateYearSemester: (Int, Int) -> Unit,
    onLogoutConfirm: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            onClearSuccessMessage()
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
                    state.error?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center).padding(16.dp)
                        )
                    }
                }
                state.profile != null -> {
                    state.profile?.let { profile ->
                        Column(
                            modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
                        ) {
                            // Header Section
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .drawBehind {
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
                                    modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(top = 24.dp)
                                ) {
                                    Text(
                                        text = "Profile",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 24.dp)
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))

                                    Card(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                        shape = RoundedCornerShape(24.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            val initials = profile.user.fullName.split(" ")
                                                .filter { it.isNotBlank() }
                                                .mapNotNull { it.firstOrNull() }
                                                .joinToString("").take(2).uppercase()

                                            Surface(
                                                shape = CircleShape,
                                                color = Color(0xFFEAF5FF),
                                                modifier = Modifier.size(80.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(text = initials, color = SimpleBlue, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(16.dp))
                                            Text(text = profile.user.fullName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E), textAlign = TextAlign.Center)
                                            Text(text = profile.user.email, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF5E6368), textAlign = TextAlign.Center)
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(text = profile.user.studentId, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF5E6368), textAlign = TextAlign.Center)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            ProfileStatsRow(uploadCount = profile.stats.uploadedPastPapers)
                            Spacer(modifier = Modifier.height(24.dp))
                            MyUploadsSection(uploadCount = profile.stats.uploadedPastPapers, onUploadClick = onNavigateToUpload)
                            Spacer(modifier = Modifier.height(24.dp))
                            SettingsSection(
                                onChangePassword = { onNavigateToForgotPassword(profile.user.email) },
                                onUpdateYearSemester = onShowYearSemesterSheet,
                                onLogout = { showLogoutDialog = true }
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(text = "UniPapers v1.0 • Made for students", style = MaterialTheme.typography.labelLarge, color = Color(0xFF5E6368), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp))
                        }
                    }
                }
            }
        }
    }

    // Bottom Sheet Logic
    if (state.showYearSemesterSheet) {
        val sheetState = rememberModalBottomSheetState()
        var selectedYear by remember { mutableIntStateOf(state.profile?.user?.yearOfStudy ?: 1) }
        var selectedSemester by remember { mutableIntStateOf(state.profile?.user?.currentSemester ?: 1) }

        ModalBottomSheet(onDismissRequest = onDismissYearSemesterSheet, sheetState = sheetState, containerColor = Color.White) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                Text(text = "Update Year / Semester", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))

                // Year Selection
                Text(text = "Year of Study", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..4).forEach { year ->
                        FilterChip(
                            selected = selectedYear == year,
                            onClick = { selectedYear = year },
                            label = { Text("Year $year") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SimpleBlue, selectedLabelColor = Color.White)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Semester Selection
                Text(text = "Semester", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..2).forEach { sem ->
                        FilterChip(
                            selected = selectedSemester == sem,
                            onClick = { selectedSemester = sem },
                            label = { Text("Semester $sem") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SimpleBlue, selectedLabelColor = Color.White)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { onUpdateYearSemester(selectedYear, selectedSemester) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = !state.isUpdatingProfile,
                    colors = ButtonDefaults.buttonColors(containerColor = SimpleBlue),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = if (state.isUpdatingProfile) "Updating..." else "Update", fontWeight = FontWeight.Bold)
                }
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
                    onLogoutConfirm()
                }) { Text("Log Out", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel", color = Color(0xFF5E6368)) }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val fakeState = ProfileState(
        profile = ProfileResponse(
            user = UserInfo(
                fullName = "Gloria Student",
                email = "gloria@example.com",
                institution = "University of Science",
                studentId = "ST12345",
                programme = "Computer Science",
                yearOfStudy = 2,
                currentSemester = 1
            ),
            stats = ProfileStats(uploadedPastPapers = 15)
        )
    )

    MaterialTheme {
        ProfileContent(
            state = fakeState,
            onNavigateToLogin = {},
            onNavigateToUpload = {},
            onNavigateToForgotPassword = {},
            onClearSuccessMessage = {},
            onShowYearSemesterSheet = {},
            onDismissYearSemesterSheet = {},
            onUpdateYearSemester = { _, _ -> },
            onLogoutConfirm = {}
        )
    }
}
