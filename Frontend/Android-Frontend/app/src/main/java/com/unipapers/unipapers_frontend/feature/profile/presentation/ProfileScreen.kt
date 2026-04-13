package com.unipapers.unipapers_frontend.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unipapers.unipapers_frontend.feature.profile.presentation.components.ChangePasswordModal
import com.unipapers.unipapers_frontend.feature.profile.presentation.components.MyUploadsSection
import com.unipapers.unipapers_frontend.feature.profile.presentation.components.ProfileStatsRow
import com.unipapers.unipapers_frontend.feature.profile.presentation.components.SettingsSection
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.foundation.layout.Row
private val NavyBlue = Color(0xFF0D1B4B)
private val Background = Color(0xFFF8F9FA)

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
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = NavyBlue
                    )
                }
                state.error != null -> {
                    Text(
                        text = state.error!!,
                        color = Color.Red,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        state.user?.let { user ->
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = user.fullName,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyBlue,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Text(
                                    text = user.email,
                                    fontSize = 13.sp,
                                    color = Color.Gray,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Text(
                                    text = "${user.programme} · Year ${user.yearOfStudy} Sem ${user.currentSemester}",
                                    fontSize = 13.sp,
                                    color = Color.Gray,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        ProfileStatsRow(user = state.user)
                        Spacer(modifier = Modifier.height(16.dp))
                        MyUploadsSection(uploadCount = state.user?.uploadCount ?: 0)
                        Spacer(modifier = Modifier.height(16.dp))
                        SettingsSection(
                            onChangePassword = { viewModel.onShowChangePasswordModal() },
                            onUpdateYearSemester = { viewModel.onShowYearSemesterSheet() },
                            onLogout = { showLogoutDialog = true }
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
                    Text("Log Out", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )

    }
    if (state.showYearSemesterSheet) {
        val sheetState = rememberModalBottomSheetState()
        var selectedYear by remember { mutableStateOf(state.user?.yearOfStudy ?: 1) }
        var selectedSemester by remember { mutableStateOf(state.user?.currentSemester ?: 1) }

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
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Year of Study", color = NavyBlue, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..4).forEach { year ->
                        FilterChip(
                            selected = selectedYear == year,
                            onClick = { selectedYear = year },
                            label = { Text("Year $year") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NavyBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Semester", color = NavyBlue, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..2).forEach { sem ->
                        FilterChip(
                            selected = selectedSemester == sem,
                            onClick = { selectedSemester = sem },
                            label = { Text("Semester $sem") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NavyBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.onUpdateYearSemester(selectedYear, selectedSemester) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
                ) {
                    Text("Update", color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}