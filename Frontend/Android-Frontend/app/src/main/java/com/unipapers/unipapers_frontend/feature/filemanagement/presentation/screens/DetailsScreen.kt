package com.unipapers.unipapers_frontend.feature.filemanagement.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unipapers.unipapers_frontend.core.ui.theme.Gray
import com.unipapers.unipapers_frontend.core.ui.theme.GrayText
import com.unipapers.unipapers_frontend.core.ui.theme.PrimaryBlue
import com.unipapers.unipapers_frontend.core.domain.model.Course

@Composable
fun DetailsScreen(
    courses: List<Course>,
    isLoadingCourses: Boolean,
    coursesError: String?,
    selectedCoursePublicId: String,
    onCourseSelected: (Course) -> Unit,
    paperType: String,
    onPaperTypeChange: (String) -> Unit,
    academicYear: String,
    onAcademicYearChange: (String) -> Unit,
    semester: String,
    onSemesterChange: (String) -> Unit,
    yearOfStudy: String,
    onYearOfStudyChange: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Paper Details",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Select paper information",
            style = MaterialTheme.typography.bodyMedium.copy(color = GrayText)
        )

        Spacer(modifier = Modifier.height(24.dp))

        CourseDetailSection(
            title = "Course Unit",
            items = courses,
            isLoading = isLoadingCourses,
            error = coursesError,
            selectedCoursePublicId = selectedCoursePublicId,
            onCourseSelected = onCourseSelected
        )

        Spacer(modifier = Modifier.height(24.dp))

        DetailSection(
            title = "Paper Type",
            items = listOf("Exam", "Test"),
            selectedItem = paperType,
            onItemSelected = onPaperTypeChange
        )

        Spacer(modifier = Modifier.height(24.dp))

        DetailSection(
            title = "Academic Year",
            items = listOf("2024/2025", "2023/2024", "2022/2023", "2021/2022"),
            selectedItem = academicYear,
            onItemSelected = onAcademicYearChange
        )

        Spacer(modifier = Modifier.height(24.dp))

        DetailSection(
            title = "Semester",
            items = listOf("Semester 1", "Semester 2"),
            selectedItem = semester,
            onItemSelected = onSemesterChange
        )

        Spacer(modifier = Modifier.height(24.dp))

        DetailSection(
            title = "Year of Study",
            items = listOf("Year 1", "Year 2", "Year 3", "Year 4"),
            selectedItem = yearOfStudy,
            onItemSelected = onYearOfStudyChange
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailSection(
    title: String,
    items: List<Course>,
    isLoading: Boolean,
    error: String?,
    selectedCoursePublicId: String,
    onCourseSelected: (Course) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedCourse = items.find { it.publicId == selectedCoursePublicId }

    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = GrayText
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = when {
                    isLoading && items.isEmpty() -> "Loading courses..."
                    error != null && items.isEmpty() -> "Error loading courses"
                    selectedCourse != null -> "${selectedCourse.courseCode} - ${selectedCourse.courseName}"
                    else -> "Select Course Unit"
                },
                onValueChange = {},
                readOnly = true,
                trailingIcon = { 
                    if (isLoading && items.isEmpty()) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = PrimaryBlue
                        )
                    } else {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Gray
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                textStyle = MaterialTheme.typography.bodyMedium
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                if (isLoading && items.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Loading courses...", color = GrayText) },
                        onClick = {},
                        enabled = false
                    )
                } else if (error != null && items.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Error: $error", color = Color.Red) },
                        onClick = {},
                        enabled = false
                    )
                } else if (items.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("No courses available", color = GrayText) },
                        onClick = {},
                        enabled = false
                    )
                } else {
                    items.forEach { item ->
                        DropdownMenuItem(
                            text = {
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text(
                                        text = item.courseCode,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    )
                                    Text(
                                        text = item.courseName,
                                        style = MaterialTheme.typography.bodySmall.copy(color = GrayText)
                                    )
                                }
                            },
                            onClick = {
                                onCourseSelected(item)
                                expanded = false
                            },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailSection(
    title: String, 
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = GrayText
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 24.dp)
        ) {
            items(items) { item ->
                SelectionChip(
                    text = item,
                    isSelected = selectedItem == item,
                    onClick = { onItemSelected(item) }
                )
            }
        }
    }
}

@Composable
fun SelectionChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) PrimaryBlue else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (isSelected) PrimaryBlue else Gray,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else GrayText,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsScreenPreview() {
    DetailsScreen(
        courses = listOf(Course(publicId = "course-1", courseCode = "CSC 1100", courseName = "Computer Science")),
        isLoadingCourses = false,
        coursesError = null,
        selectedCoursePublicId = "",
        onCourseSelected = {},
        paperType = "",
        onPaperTypeChange = {},
        academicYear = "",
        onAcademicYearChange = {},
        semester = "",
        onSemesterChange = {},
        yearOfStudy = "",
        onYearOfStudyChange = {}
    )
}
