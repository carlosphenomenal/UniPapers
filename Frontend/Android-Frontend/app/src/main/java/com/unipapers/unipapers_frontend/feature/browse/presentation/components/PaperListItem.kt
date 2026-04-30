package com.unipapers.unipapers_frontend.feature.browse.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unipapers.unipapers_frontend.core.domain.model.Paper
import com.unipapers.unipapers_frontend.core.ui.theme.GrayText
import com.unipapers.unipapers_frontend.core.ui.theme.PrimaryBlue

@Composable
fun PaperListItem(
    paper: Paper,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Type Badge
                Box(
                    modifier = Modifier
                        .background(
                            color = PrimaryBlue,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = paper.type.displayName,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Academic Year
                Text(
                    text = paper.academicYear,
                    color = GrayText,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = paper.courseName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E)
                ),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoItem(icon = Icons.Default.Description, text = paper.courseCode)
                InfoItem(icon = Icons.Default.Groups, text = "Year ${paper.yearOfStudy}")
                InfoItem(icon = Icons.Default.FileDownload, text = paper.downloadCount.toString())
                InfoItem(icon = Icons.Default.InsertDriveFile, text = "${paper.pageCount}p")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                paper.tags.take(3).forEach { tag ->
                    TagItem(text = tag)
                }
                if (paper.tags.size > 3) {
                    TagItem(text = "+${paper.tags.size - 3}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f))
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildString {
                        append("by ")
                        append(paper.uploaderName)
                    },
                    color = GrayText,
                    fontSize = 13.sp
                )
                Text(
                    text = paper.uploadDate,
                    color = GrayText,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun InfoItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GrayText,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            color = Color.Black.copy(alpha = 0.7f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun TagItem(text: String) {
    Box(
        modifier = Modifier
            .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = GrayText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
