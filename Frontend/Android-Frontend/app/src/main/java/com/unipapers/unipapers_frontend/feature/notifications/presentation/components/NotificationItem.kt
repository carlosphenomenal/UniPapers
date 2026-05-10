package com.unipapers.unipapers_frontend.feature.notifications.presentation.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
    onClick: () -> Unit = {}
) {

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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Icon circle
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Message and time
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.message,
                fontSize = 14.sp,
                fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatTimeAgo(notification.createdAt),
                fontSize = 12.sp,
                color = Color(0xFF9E9E9E)
            )
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
