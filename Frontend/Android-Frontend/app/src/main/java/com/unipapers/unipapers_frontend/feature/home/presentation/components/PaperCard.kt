package com.unipapers.unipapers_frontend.feature.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unipapers.unipapers_frontend.feature.home.domain.Paper
import com.unipapers.unipapers_frontend.feature.home.domain.PaperType

val NavyBlue = Color(0xFF0D1B4B)
val Amber = Color(0xFFF5A623)
val NearWhite = Color(0xFFF8F9FA)
val TealGreen = Color(0xFF00897B)
val MediumGray = Color(0xFF9E9E9E)
val LightGray = Color(0xFFE0E0E0)

@Composable
fun PaperCard(paper: Paper) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(150.dp)
            .clickable { },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Paper type pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        when (paper.type) {
                            PaperType.EXAM -> NavyBlue
                            PaperType.TEST_CAT -> TealGreen
                            PaperType.LECTURE_NOTES -> MediumGray
                        }
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = paper.type.displayName,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Paper title
            Text(
                text = paper.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = NavyBlue,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Bottom info
            Column {
                Text(
                    text = paper.courseUnit,
                    fontSize = 11.sp,
                    color = MediumGray
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = paper.uploadedBy,
                        fontSize = 10.sp,
                        color = MediumGray
                    )
                    Text(
                        text = paper.timeAgo,
                        fontSize = 10.sp,
                        color = MediumGray
                    )
                }
            }
        }
    }
}