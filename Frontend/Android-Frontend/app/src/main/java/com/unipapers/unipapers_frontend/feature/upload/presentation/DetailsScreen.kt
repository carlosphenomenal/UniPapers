package com.unipapers.unipapers_frontend.feature.upload.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unipapers.unipapers_frontend.core.ui.theme.Gray
import com.unipapers.unipapers_frontend.core.ui.theme.GrayText
import com.unipapers.unipapers_frontend.core.ui.theme.LightGray
import com.unipapers.unipapers_frontend.core.ui.theme.PrimaryBlue

@Composable
fun DetailsScreen(
    courseUnit: String,
    onCourseUnitChange: (String) -> Unit,
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

        DetailSection(
            title = "Course Unit",
            items = listOf("CSC 1100", "CSC 1200", "CSC 2100", "CSC 2200"),
            selectedItem = courseUnit,
            onItemSelected = onCourseUnitChange
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
    DetailsScreen(courseUnit = "", onCourseUnitChange = {}, paperType = "", onPaperTypeChange = {}, academicYear = "", onAcademicYearChange = {}, semester = "", onSemesterChange = {}, yearOfStudy = "", onYearOfStudyChange = {})
}