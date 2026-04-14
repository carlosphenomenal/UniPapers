package com.unipapers.unipapers_frontend.feature.home.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unipapers.unipapers_frontend.feature.home.domain.Paper
import com.unipapers.unipapers_frontend.core.ui.theme.MediumGray

@Composable
fun RecentPapersRow(papers: List<Paper>) {
    if (papers.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(150.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No papers found.",
                color = MediumGray,
                fontSize = 13.sp
            )
        }
    } else {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(papers) { paper ->
                PaperCard(paper = paper)
            }
        }
    }
}