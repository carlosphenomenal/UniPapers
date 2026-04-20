package com.unipapers.unipapers_frontend.feature.filemanagement.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unipapers.unipapers_frontend.R
import com.unipapers.unipapers_frontend.core.ui.theme.SimpleBlue
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.model.Download
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.model.DownloadStatus
import com.unipapers.unipapers_frontend.feature.filemanagement.presentation.DownloadViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    viewModel: DownloadViewModel = hiltViewModel()
) {
    val downloads by viewModel.downloads.collectAsState()

    val activeDownloads = downloads.filter { 
        it.status == DownloadStatus.DOWNLOADING || 
        it.status == DownloadStatus.PAUSED || 
        it.status == DownloadStatus.PENDING || 
        it.status == DownloadStatus.WAITING_FOR_NETWORK 
    }
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
                items(activeDownloads, key = { it.downloadId }) { download ->
                    DownloadCard(
                        download = download,
                        onPause = { viewModel.pauseDownload(download.downloadId) },
                        onResume = { viewModel.resumeDownload(download.downloadId) },
                        onDelete = { viewModel.cancelDownload(download.downloadId) }
                    )
                }
            }

            if (failedDownloads.isNotEmpty()) {
                item { SectionHeader(stringResource(R.string.failed_section)) }
                items(failedDownloads, key = { it.downloadId }) { download ->
                    DownloadCard(
                        download = download,
                        onResume = { viewModel.resumeDownload(download.downloadId) },
                        onDelete = { viewModel.cancelDownload(download.downloadId) }
                    )
                }
            }

            if (completedDownloads.isNotEmpty()) {
                item { SectionHeader(stringResource(R.string.completed_section)) }
                items(completedDownloads, key = { it.downloadId }) { download ->
                    DownloadCard(
                        download = download,
                        onDelete = { viewModel.cancelDownload(download.downloadId) }
                    )
                }
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
    download: Download,
    modifier: Modifier = Modifier,
    onPause: () -> Unit = {},
    onResume: () -> Unit = {},
    onDelete: () -> Unit = {}
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
                Text(
                    text = download.fileName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SimpleBlue
                    ),
                    maxLines = 1
                )
                
                if (download.status != DownloadStatus.COMPLETED) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { download.progressPercent / 100f },
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
                        val statusText = when (download.status) {
                            DownloadStatus.DOWNLOADING -> stringResource(R.string.downloading)
                            DownloadStatus.PAUSED -> stringResource(R.string.paused)
                            DownloadStatus.FAILED -> stringResource(R.string.failed)
                            DownloadStatus.PENDING -> stringResource(R.string.pending)
                            DownloadStatus.WAITING_FOR_NETWORK -> stringResource(R.string.waiting_for_network)
                            else -> ""
                        }
                        val statusColor = when (download.status) {
                            DownloadStatus.DOWNLOADING -> Color(0xFF3B82F6)
                            DownloadStatus.PAUSED -> Color(0xFFF59E0B)
                            DownloadStatus.FAILED -> Color(0xFFEF4444)
                            DownloadStatus.WAITING_FOR_NETWORK -> Color(0xFFF59E0B)
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
                            text = "${download.progressPercent}% • ${formatBytes(download.totalBytes)}",
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
                        val dateText = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(download.updatedAtMillis))
                        Text(
                            text = "${formatBytes(download.totalBytes)} • $dateText",
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
                if (download.status == DownloadStatus.DOWNLOADING || download.status == DownloadStatus.PENDING) {
                    IconButton(onClick = onPause, modifier = Modifier.size(24.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_pause),
                            contentDescription = stringResource(R.string.content_desc_pause),
                            tint = Color(0xFF64748B)
                        )
                    }
                } else if (download.status == DownloadStatus.PAUSED || download.status == DownloadStatus.FAILED || download.status == DownloadStatus.WAITING_FOR_NETWORK) {
                    IconButton(onClick = onResume, modifier = Modifier.size(24.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_play),
                            contentDescription = stringResource(R.string.content_desc_resume),
                            tint = SimpleBlue
                        )
                    }
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_trash),
                        contentDescription = stringResource(R.string.content_desc_delete),
                        tint = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
    return String.format("%.1f %s", bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}
