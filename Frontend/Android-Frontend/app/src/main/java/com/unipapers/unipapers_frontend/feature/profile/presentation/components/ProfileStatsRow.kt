package com.unipapers.unipapers_frontend.feature.profile.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unipapers.unipapers_frontend.core.ui.theme.PrimaryBlue
import com.unipapers.unipapers_frontend.core.ui.theme.PrimaryOrange
import com.unipapers.unipapers_frontend.feature.profile.domain.model.UserStats

@Composable
fun ProfileStatsRow(stats: UserStats) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryBlue
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            StatItem("Uploads", stats.documentsUploaded.toString())
            StatItem("Downloads", stats.totalDownloads.toString())
            StatItem("Free Views", stats.totalFreeViewPoints.toString())

        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = value,
            color = PrimaryOrange,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall
        )
    }
}