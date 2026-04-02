package com.unipapers.unipapers_frontend.feature.upload.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unipapers.unipapers_frontend.core.ui.theme.DividerColor
import com.unipapers.unipapers_frontend.core.ui.theme.GrayText
import com.unipapers.unipapers_frontend.core.ui.theme.LightGray
import com.unipapers.unipapers_frontend.core.ui.theme.PrimaryBlue
import com.unipapers.unipapers_frontend.core.ui.theme.SuccessGreen

@Composable
fun StepItem(number: Int, label: String, isSelected: Boolean, isCompleted: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val backgroundColor = when {
            isCompleted -> SuccessGreen
            isSelected -> PrimaryBlue
            else -> LightGray
        }
        
        val contentColor = if (isSelected || isCompleted) Color.White else GrayText
        val labelColor = if (isSelected) PrimaryBlue else GrayText

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completed",
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
            } else {
                Text(
                    text = number.toString(),
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = labelColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}

@Composable
fun UploadStepBar(activeStep: Int) {
    val stepTitles = listOf("File", "Details", "Tags", "Review")

    Row(verticalAlignment = Alignment.CenterVertically) {
        stepTitles.forEachIndexed { index, title ->
            val isCompleted = index < activeStep
            val isSelected = index == activeStep

            StepItem(
                number = index + 1,
                label = title,
                isSelected = isSelected,
                isCompleted = isCompleted
            )

            if (index != stepTitles.lastIndex) {
                val dividerColor = if (index < activeStep) SuccessGreen else DividerColor
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 16.dp, start = 8.dp, end = 8.dp),
                    color = dividerColor,
                    thickness = 2.dp
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun StepItemPreview(){
    StepItem(number = 1, label = "File", isSelected = true, isCompleted = false)
}

@Composable
@Preview(showBackground = true)
fun StepItemCompletedPreview(){
    StepItem(number = 1, label = "File", isSelected = false, isCompleted = true)
}
