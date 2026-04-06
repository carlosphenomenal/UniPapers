package com.unipapers.unipapers_frontend.feature.filemanagement.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unipapers.unipapers_frontend.core.ui.theme.GrayText
import com.unipapers.unipapers_frontend.core.ui.theme.PrimaryBlue
import com.unipapers.unipapers_frontend.core.ui.theme.PrimaryOrange

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReviewSubmitScreen(
    courseUnit: String,
    paperType: String,
    academicYear: String,
    semester: String,
    yearOfStudy: String,
    fileName: String,
    selectedTags: List<String>
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Review & Submit",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Review your submission before uploading",
            style = MaterialTheme.typography.bodyMedium.copy(color = GrayText)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                ReviewRow(label = "Course Unit", value = courseUnit)
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Paper Type",
                        style = MaterialTheme.typography.bodyMedium.copy(color = GrayText)
                    )
                    Surface(
                        color = PrimaryBlue,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = paperType,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                ReviewRow(label = "Academic Year", value = academicYear)
                Spacer(modifier = Modifier.height(16.dp))
                ReviewRow(label = "Semester", value = semester)
                Spacer(modifier = Modifier.height(16.dp))
                ReviewRow(label = "Year of Study", value = yearOfStudy)
                Spacer(modifier = Modifier.height(16.dp))
                ReviewRow(label = "Files", value = fileName)

                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Confirmed Tags",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.bodyMedium.copy(color = GrayText)
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedTags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .border(
                                    width = 1.dp,
                                    color = PrimaryOrange,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .background(
                                    color = PrimaryOrange.copy(alpha = 0.05f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = PrimaryOrange,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ReviewRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = GrayText)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
