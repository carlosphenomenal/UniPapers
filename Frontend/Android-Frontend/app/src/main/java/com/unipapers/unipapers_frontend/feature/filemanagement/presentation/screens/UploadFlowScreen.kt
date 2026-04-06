package com.unipapers.unipapers_frontend.feature.filemanagement.presentation.screens

import android.net.Uri
import androidx.annotation.RawRes
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.unipapers.unipapers_frontend.R
import com.unipapers.unipapers_frontend.core.navigation.PlaceholderScreen
import com.unipapers.unipapers_frontend.core.ui.components.ToastManager
import com.unipapers.unipapers_frontend.core.ui.theme.GrayText
import com.unipapers.unipapers_frontend.core.ui.theme.NextButtonColor
import com.unipapers.unipapers_frontend.core.ui.theme.PrimaryBlue
import com.unipapers.unipapers_frontend.feature.filemanagement.presentation.viewmodels.FileUploadViewModel
import com.unipapers.unipapers_frontend.feature.filemanagement.presentation.viewmodels.UploadStatus
import com.unipapers.unipapers_frontend.feature.filemanagement.utils.PaperFileUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadFlowScreen(
    onBackClick: () -> Unit,
    toastManager: ToastManager,
    viewModel: FileUploadViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val uploadStatus by viewModel.status
    
    var activeStep by remember { mutableIntStateOf(0) }
    
    // Valid options for strict validation (matching DetailsScreen)
    val courseUnits = listOf("CSC 1100", "CSC 1200", "CSC 2100", "CSC 2200")
    val paperTypes = listOf("Exam", "Test")
    val academicYears = listOf("2024/2025", "2023/2024", "2022/2023", "2021/2022")
    val semesters = listOf("Semester 1", "Semester 2")
    val yearOfStudies = listOf("Year 1", "Year 2", "Year 3", "Year 4")

    // State for validation
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var courseUnit by remember { mutableStateOf("") }
    var paperType by remember { mutableStateOf("") }
    var academicYear by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("") }
    var yearOfStudy by remember { mutableStateOf("") }
    
    // Tags state
    var allTags by remember { mutableStateOf(listOf("Binary Trees", "Graphs", "Sorting Algorithms", "Big-O Notation", "Dynamic Programming", "Hashing", "Linked Lists")) }
    var selectedTags by remember { mutableStateOf(emptyList<String>()) }

    // Sync Gemini Metadata to local state when it arrives
    LaunchedEffect(uiState.geminiMetadata) {
        uiState.geminiMetadata?.let { metadata ->
            if (courseUnit.isEmpty()) {
                metadata.courseCode?.let { if (courseUnits.contains(it)) courseUnit = it }
            }
            if (academicYear.isEmpty()) {
                metadata.academicYear?.let { if (academicYears.contains(it)) academicYear = it }
            }
            if (semester.isEmpty()) {
                metadata.semester?.let { if (semesters.contains(it)) semester = it }
            }
            if (yearOfStudy.isEmpty()) {
                metadata.yearOfStudy?.let { if (yearOfStudies.contains(it)) yearOfStudy = it }
            }
            if (paperType.isEmpty()) {
                metadata.paperType?.let { if (paperTypes.contains(it)) paperType = it }
            }
        }
    }

    // Update tags when Gemini returns them
    LaunchedEffect(uiState.suggestedTags) {
        if (uiState.suggestedTags.isNotEmpty()) {
            allTags = uiState.suggestedTags
            selectedTags = uiState.suggestedTags
        }
    }

    // Handle Upload Status Changes
    LaunchedEffect(uploadStatus) {
        when (uploadStatus) {
            is UploadStatus.Success -> {
                toastManager.showToast((uploadStatus as UploadStatus.Success).message)
                onBackClick() // Or navigate to success screen
            }
            is UploadStatus.Error -> {
                toastManager.showToast((uploadStatus as UploadStatus.Error).message)
            }
            else -> {}
        }
    }

    // Handle Gemini Errors
    LaunchedEffect(uiState.tagError) {
        uiState.tagError?.let { error ->
            toastManager.showToast(error)
        }
    }

    val isStep0Valid = selectedFileUri != null
    val isStep1Valid = courseUnits.contains(courseUnit) && 
                      paperTypes.contains(paperType) && 
                      academicYears.contains(academicYear) && 
                      semesters.contains(semester) && 
                      yearOfStudies.contains(yearOfStudy)
    val isStep2Valid = selectedTags.isNotEmpty()

    val isNextEnabled = when (activeStep) {
        0 -> isStep0Valid
        1 -> isStep1Valid
        2 -> isStep2Valid
        3 -> true // Review step
        else -> false
    }

    val canProceed = isNextEnabled && !uiState.isLoadingTags && uploadStatus !is UploadStatus.Loading && uploadStatus !is UploadStatus.Hashing

    val handleSubmit = {
        val uri = selectedFileUri
        if (uri != null) {
            val file = PaperFileUtils.copyUriToFile(context, uri)
            if (file != null) {
                // Prepare ViewModel state
                viewModel.courseName.value = courseUnit
                viewModel.type.value = paperType
                viewModel.academicYear.value = academicYear
                viewModel.semester.intValue = when(semester) {
                    "Semester 1" -> 1
                    "Semester 2" -> 2
                    else -> 1
                }
                viewModel.yearOfStudy.intValue = yearOfStudy.replace("Year ", "").toIntOrNull() ?: 1
                
                // Sync tags
                viewModel.topicsNames.clear()
                viewModel.topicsNames.addAll(selectedTags)
                
                // Set file name if not already set
                viewModel.fileName.value = uiState.selectedFileName
                
                viewModel.uploadFile(file)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Upload Paper",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (activeStep > 0) {
                            activeStep -= 1
                        } else {
                            onBackClick()
                        }
                    }, enabled = uploadStatus !is UploadStatus.Loading) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryBlue
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Stepper
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                UploadStepBar(activeStep = activeStep)
            }

            Spacer(modifier = Modifier.height(40.dp))
            
            Box(modifier = Modifier.weight(1f)) {
                if (uiState.isLoadingTags) {
                    GeminiLoadingScreen()
                } else if (uploadStatus is UploadStatus.Loading || uploadStatus is UploadStatus.Hashing) {
                    SubmissionLoadingScreen(status = uploadStatus)
                } else {
                    when (activeStep) {
                        0 -> SelectFileScreen(
                            selectedFileUri = selectedFileUri,
                            onFileSelected = { selectedFileUri = it },
                            toastManager = toastManager
                        )
                        1 -> DetailsScreen(
                            courseUnit = courseUnit,
                            onCourseUnitChange = { courseUnit = it },
                            paperType = paperType,
                            onPaperTypeChange = { paperType = it },
                            academicYear = academicYear,
                            onAcademicYearChange = { academicYear = it },
                            semester = semester,
                            onSemesterChange = { semester = it },
                            yearOfStudy = yearOfStudy,
                            onYearOfStudyChange = { yearOfStudy = it }
                        )
                        2 -> TagsSelectionScreen(
                            allTags = allTags,
                            selectedTags = selectedTags,
                            onTagToggle = { tag ->
                                selectedTags = if (selectedTags.contains(tag)) {
                                    selectedTags.filter { it != tag }
                                } else {
                                    selectedTags + tag
                                }
                            },
                            onAddTag = { newTag ->
                                if (!allTags.contains(newTag)) {
                                    allTags = allTags + newTag
                                    selectedTags = selectedTags + newTag
                                }
                            }
                        )
                        3 -> ReviewSubmitScreen(
                            courseUnit = courseUnit,
                            paperType = paperType,
                            academicYear = academicYear,
                            semester = semester,
                            yearOfStudy = yearOfStudy,
                            fileName = uiState.selectedFileName.ifEmpty { "Selected File" },
                            selectedTags = selectedTags
                        )
                        else -> PlaceholderScreen("Step ${activeStep + 1}")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { 
                    if (activeStep < 3) {
                        if (activeStep == 0) {
                            selectedFileUri?.let { uri ->
                                val name = PaperFileUtils.getFileNameFromUri(context, uri)
                                val size = "${PaperFileUtils.getFileSizeFromUri(context, uri) / 1024} KB"
                                viewModel.onFileSelected(uri, name, size)
                                viewModel.onStep1Next() // Triggers Gemini analysis
                            }
                        }
                        activeStep++ 
                    } else {
                        handleSubmit()
                    }
                },
                enabled = canProceed,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canProceed) PrimaryBlue else NextButtonColor,
                    disabledContainerColor = NextButtonColor,
                    contentColor = Color.White,
                    disabledContentColor = Color.White.copy(alpha = 0.6f)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (activeStep == 3) "Submit Paper" else "Next",
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (activeStep < 3) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun GeminiLoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieLoadingAnimation(
            animationResId = R.raw.loading_animation,
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Analysing your paper...",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Our AI is extracting metadata and suggesting relevant topics for you.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = GrayText,
                lineHeight = 20.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun SubmissionLoadingScreen(status: UploadStatus) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieLoadingAnimation(
            animationResId = R.raw.uploading,
            modifier = Modifier.size(200.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = when(status) {
                is UploadStatus.Hashing -> "Preparing your file..."
                is UploadStatus.Loading -> "Uploading to UniPapers..."
                else -> "Please wait..."
            },
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "This may take a moment depending on your file size.",
            style = MaterialTheme.typography.bodyMedium.copy(color = GrayText),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun LottieLoadingAnimation(
    @RawRes animationResId: Int,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(animationResId)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}

@Composable
@Preview(showBackground = true)
fun LottieLoadingAnimationPreview() {
    GeminiLoadingScreen()
}
