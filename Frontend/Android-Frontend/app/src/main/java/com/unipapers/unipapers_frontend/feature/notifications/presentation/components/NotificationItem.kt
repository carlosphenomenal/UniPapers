package com.unipapers.unipapers_frontend.feature.notifications.presentation.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unipapers.unipapers_frontend.core.ui.theme.Amber
import com.unipapers.unipapers_frontend.core.ui.theme.NavyBlue
import com.unipapers.unipapers_frontend.feature.notifications.domain.Notification
import com.unipapers.unipapers_frontend.feature.notifications.domain.NotificationType
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationItem(
    notification: Notification,
    onMarkAsRead: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isOverflowing by remember { mutableStateOf(false) }
    val backgroundColor = if (notification.isRead) Color.White else Color(0xFFE8F0FE)

    val icon: ImageVector = when (notification.type) {
        NotificationType.UPLOAD_CONFIRMED -> Icons.Default.Upload
        NotificationType.NEW_PAPER -> Icons.Default.Description
        NotificationType.PAPER_FLAGGED -> Icons.Default.Flag
        NotificationType.FLAG_RESOLVED -> Icons.Default.CheckCircle
        NotificationType.WELCOME -> Icons.Default.Notifications
        NotificationType.GENERAL -> Icons.Default.Notifications
    }

    val iconColor: Color = when (notification.type) {
        NotificationType.UPLOAD_CONFIRMED -> NavyBlue
        NotificationType.NEW_PAPER -> NavyBlue
        NotificationType.PAPER_FLAGGED -> Color(0xFFE53935)
        NotificationType.FLAG_RESOLVED -> Color(0xFF43A047)
        NotificationType.WELCOME -> Amber
        NotificationType.GENERAL -> NavyBlue
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable(onClick = {
                if (!notification.isRead) onMarkAsRead()
                onClick()
            }),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon circle
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = notification.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyBlue,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = formatTimeAgo(notification.createdAt),
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Row {
                    if (!notification.isRead) {
                        IconButton(
                            onClick = onMarkAsRead,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Mark as read",
                                tint = Color(0xFF43A047),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                Column {
                    Text(
                        text = notification.message,
                        fontSize = 14.sp,
                        fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Medium,
                        color = Color(0xFF333333),
                        lineHeight = 20.sp,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = { textLayoutResult ->
                            if (!isExpanded) {
                                isOverflowing = textLayoutResult.hasVisualOverflow
                            }
                        }
                    )
                    
                    if (isOverflowing || isExpanded) {
                        IconButton(
                            onClick = { isExpanded = !isExpanded },
                            modifier = Modifier
                                .align(Alignment.End)
                                .size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isExpanded) "Show less" else "Show more",
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTimeAgo(createdAt: String): String {
    val instant = try {
        Instant.parse(createdAt)
    } catch (e: Exception) {
        return createdAt
    }

    val now = Instant.now()
    val duration = Duration.between(instant, now).coerceAtLeast(Duration.ZERO)

    val minutes = duration.toMinutes()
    val hours = duration.toHours()
    val days = duration.toDays()

    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days < 7 -> "${days}d ago"
        else -> DateTimeFormatter.ofPattern("MMM d, yyyy")
            .withZone(ZoneId.systemDefault())
            .format(instant)
    }
}
