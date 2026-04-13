package com.unipapers.unipapers_frontend.feature.home.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unipapers.unipapers_frontend.feature.home.domain.PaperType

@Composable
fun FilterChipsRow(
    selectedFilter: PaperType?,
    onFilterSelected: (PaperType?) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "All" chip
        FilterChip(
            selected = selectedFilter == null,
            onClick = { onFilterSelected(null) },
            label = { Text("All", fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = NavyBlue,
                selectedLabelColor = Color.White,
                containerColor = Color.White,
                labelColor = NavyBlue
            )
        )

        // One chip per paper type
        PaperType.entries.forEach { type ->
            FilterChip(
                selected = selectedFilter == type,
                onClick = { onFilterSelected(type) },
                label = { Text(type.displayName, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NavyBlue,
                    selectedLabelColor = Color.White,
                    containerColor = Color.White,
                    labelColor = NavyBlue
                )
            )
        }
    }
}