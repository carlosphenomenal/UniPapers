package com.unipapers.unipapers_frontend.feature.browse.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unipapers.unipapers_frontend.core.domain.model.PaperType
import com.unipapers.unipapers_frontend.core.ui.theme.PrimaryBlue

@Composable
fun PaperTypeTabRow(
    selectedType: PaperType,
    onTypeSelected: (PaperType) -> Unit
) {
    val tabs = listOf(PaperType.EXAM, PaperType.TEST)
    val selectedIndex = tabs.indexOf(selectedType)

    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = PrimaryBlue,
        contentColor = Color.White,
        indicator = { tabPositions ->
            if (selectedIndex < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    color = Color(0xFFFFA000), // Orange/Gold indicator as seen in image
                    height = 3.dp
                )
            }
        },
        divider = {}
    ) {
        tabs.forEach { type ->
            Tab(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                text = {
                    Text(
                        text = type.displayName,
                        fontSize = 15.sp,
                        fontWeight = if (selectedType == type) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedType == type) Color.White else Color.White.copy(alpha = 0.7f)
                    )
                }
            )
        }
    }
}
