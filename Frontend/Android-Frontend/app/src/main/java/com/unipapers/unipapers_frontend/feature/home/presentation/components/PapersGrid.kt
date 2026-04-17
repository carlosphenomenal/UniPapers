package com.unipapers.unipapers_frontend.feature.home.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unipapers.unipapers_frontend.feature.home.domain.model.Paper

@Composable
fun PapersGrid(
    papers: List<Paper>,
    onPaperClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        papers.chunked(2).forEach { rowPapers ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowPapers.forEach { paper ->
                    PaperCard(
                        paper = paper,
                        onPaperClick = onPaperClick,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill the empty space if there's only one item in the row
                if (rowPapers.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
