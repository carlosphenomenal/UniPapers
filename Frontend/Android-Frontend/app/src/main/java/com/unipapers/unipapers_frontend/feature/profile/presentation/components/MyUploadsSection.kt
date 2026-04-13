package com.unipapers.unipapers_frontend.feature.profile.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val NavyBlue = Color(0xFF0D1B4B)

@Composable
fun MyUploadsSection(uploadCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "My Uploads",
            color = NavyBlue,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (uploadCount == 0) {
            Text(
                text = "You haven't uploaded any papers yet.",
                color = Color.Gray,
                fontSize = 14.sp
            )
        } else {
            Text(
                text = "You have uploaded $uploadCount paper(s).",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}