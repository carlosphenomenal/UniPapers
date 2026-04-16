package com.unipapers.unipapers_frontend.feature.profile.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsSection(
    onChangePassword: () -> Unit,
    onUpdateYearSemester: () -> Unit,
    onLogout: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Text(
            text = "Settings",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsItem(
            icon = Icons.Default.Lock,
            label = "Change Password",
            onClick = onChangePassword
        )

        HorizontalDivider()

        SettingsItem(
            icon = Icons.Default.School,
            label = "Update Year / Semester",
            onClick = onUpdateYearSemester
        )

        HorizontalDivider()

        SettingsItem(
            icon = Icons.AutoMirrored.Filled.Logout,
            label = "Log Out",
            isDestructive = true,
            onClick = onLogout
        )
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    label: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {

    val color =
        if (isDestructive)
            MaterialTheme.colorScheme.error
        else
            MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(icon, contentDescription = label, tint = color)

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = label,
            color = color,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null
        )
    }
}