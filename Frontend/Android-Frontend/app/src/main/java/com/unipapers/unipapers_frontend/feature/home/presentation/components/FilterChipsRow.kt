package com.unipapers.unipapers_frontend.feature.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unipapers.unipapers_frontend.core.ui.theme.LightGray
import com.unipapers.unipapers_frontend.core.ui.theme.SimpleBlue
import com.unipapers.unipapers_frontend.feature.home.domain.PaperType

@Composable
fun FilterChipsRow(
    selectedFilter: PaperType?,
    onFilterSelected: (PaperType?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "All" chip
        FilterChipItem(
            label = "All",
            isSelected = selectedFilter == null,
            onClick = { onFilterSelected(null) }
        )

        // Assessment types
        listOf(
            "EXAMS" to PaperType.EXAM,
            "TESTS" to PaperType.TEST,
            "NOTES" to PaperType.NOTES,
            "ASSIGNMENTS" to PaperType.ASSIGNMENT
        ).forEach { (label, type) ->
            FilterChipItem(
                label = label,
                isSelected = selectedFilter == type,
                onClick = { onFilterSelected(type) }
            )
        }
    }
}

@Composable
fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (isSelected) SimpleBlue else Color.White)
            .border(
                width = 1.dp,
                color = if (isSelected) SimpleBlue else LightGray,
                shape = CircleShape
            )
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else Color.Gray,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}