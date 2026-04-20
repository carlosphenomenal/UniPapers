package com.unipapers.unipapers_frontend.feature.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.unipapers.unipapers_frontend.feature.home.domain.model.Paper

@Composable
fun RecentPapersRow(
    papers: List<Paper>,
    onPaperClick: (String) -> Unit,
    onDownloadClick: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(papers) { paper ->
            PaperCard(
                paper = paper,
                onPaperClick = onPaperClick,
                onDownloadClick = onDownloadClick
            )
        }
    }
}
