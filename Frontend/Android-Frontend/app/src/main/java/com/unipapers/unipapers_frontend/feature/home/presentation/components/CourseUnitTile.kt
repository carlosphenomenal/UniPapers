package com.unipapers.unipapers_frontend.feature.home.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unipapers.unipapers_frontend.feature.home.domain.CourseUnit
import com.unipapers.unipapers_frontend.core.ui.theme.NavyBlue
import com.unipapers.unipapers_frontend.core.ui.theme.MediumGray

@Composable
fun CourseUnitTile(unit: CourseUnit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable { },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = unit.code,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = NavyBlue
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = unit.name,
                fontSize = 12.sp,
                color = Color(0xFF444444),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${unit.paperCount} papers",
                fontSize = 11.sp,
                color = MediumGray
            )
        }
    }
}