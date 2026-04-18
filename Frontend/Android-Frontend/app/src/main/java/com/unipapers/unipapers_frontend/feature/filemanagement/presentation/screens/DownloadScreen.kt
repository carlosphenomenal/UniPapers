package com.unipapers.unipapers_frontend.feature.filemanagement.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unipapers.unipapers_frontend.R
import com.unipapers.unipapers_frontend.core.ui.theme.SimpleBlue
import com.unipapers.unipapers_frontend.core.ui.theme.UniPapersTheme

enum class DownloadStatus {
    DOWNLOADING, PAUSED, FAILED, COMPLETED
}

data class DownloadItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val year: String,
    val progress: Float,
    val sizeText: String,
    val status: DownloadStatus,
    val dateText: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val downloads = listOf(
        DownloadItem("1", "MTH201", "Linear Algebra", "2023", 0.64f, "1.8 MB", DownloadStatus.DOWNLOADING),
        DownloadItem("2", "PHY101", "Mechanics & Waves", "2024", 0.32f, "3.1 MB", DownloadStatus.PAUSED),
        DownloadItem("3", "ENG102", "Technical Writing", "2022", 0.45f, "1.2 MB", DownloadStatus.FAILED),
        DownloadItem("4", "CSC301", "Database Systems", "2024", 1.0f, "2.4 MB", DownloadStatus.COMPLETED, "Today, 2:30 PM"),
        DownloadItem("5", "CSC105", "Intro to Computing", "2024", 1.0f, "3.0 MB", DownloadStatus.COMPLETED, "Yesterday, 4:15 PM"),
        DownloadItem("6", "MTH101", "Calculus I", "2023", 1.0f, "2.1 MB", DownloadStatus.COMPLETED, "Monday, 10:00 AM")
    )

    val activeDownloads = downloads.filter { it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.PAUSED }
    val failedDownloads = downloads.filter { it.status == DownloadStatus.FAILED }
    val completedDownloads = downloads.filter { it.status == DownloadStatus.COMPLETED }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.downloads_title),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SimpleBlue
                            )
                        )
                        Text(
                            text = stringResource(R.string.files_count, downloads.size),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.content_desc_back),
                            tint = Color(0xFF1E293B)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp, top = 16.dp)
        ) {
            if (activeDownloads.isNotEmpty()) {
                item { SectionHeader(stringResource(R.string.active_section)) }
                items(activeDownloads) { DownloadCard(it) }
            }

            if (failedDownloads.isNotEmpty()) {
                item { SectionHeader(stringResource(R.string.failed_section)) }
                items(failedDownloads) { DownloadCard(it) }
            }

            if (completedDownloads.isNotEmpty()) {
                item { SectionHeader(stringResource(R.string.completed_section)) }
                items(completedDownloads) { DownloadCard(it) }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            letterSpacing = 1.sp
        ),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun DownloadCard(
    item: DownloadItem,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // File Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEFF6FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_file_text),
                    contentDescription = null,
                    tint = SimpleBlue,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SimpleBlue
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = item.year,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SimpleBlue
                            )
                        )
                    }
                }
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF64748B)
                    )
                )

                if (item.status != DownloadStatus.COMPLETED) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { item.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = SimpleBlue,
                        trackColor = Color(0xFFF1F5F9)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val statusText = when (item.status) {
                            DownloadStatus.DOWNLOADING -> stringResource(R.string.downloading)
                            DownloadStatus.PAUSED -> stringResource(R.string.paused)
                            DownloadStatus.FAILED -> stringResource(R.string.failed)
                            else -> ""
                        }
                        val statusColor = when (item.status) {
                            DownloadStatus.DOWNLOADING -> Color(0xFF3B82F6)
                            DownloadStatus.PAUSED -> Color(0xFFF59E0B)
                            DownloadStatus.FAILED -> Color(0xFFEF4444)
                            else -> Color(0xFF64748B)
                        }
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        )
                        Text(
                            text = "${(item.progress * 100).toInt()}% • ${item.sizeText}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF64748B)
                            )
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_check),
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${item.sizeText} • ${item.dateText}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF64748B)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Actions
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (item.status == DownloadStatus.DOWNLOADING) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pause),
                        contentDescription = stringResource(R.string.content_desc_pause),
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(24.dp)
                    )
                } else if (item.status == DownloadStatus.PAUSED || item.status == DownloadStatus.FAILED) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_play),
                        contentDescription = stringResource(R.string.content_desc_resume),
                        tint = SimpleBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Icon(
                    painter = painterResource(id = R.drawable.ic_trash),
                    contentDescription = stringResource(R.string.content_desc_delete),
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DownloadScreenPreview() {
    UniPapersTheme {
        DownloadScreen()
    }
}