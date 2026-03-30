package com.unipapers.unipapers_frontend.feature.upload.presentation

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unipapers.unipapers_frontend.core.ui.components.ToastManager
import com.unipapers.unipapers_frontend.core.ui.theme.*
import kotlinx.coroutines.launch

private const val MAX_FILE_SIZE_BYTES = 20 * 1024 * 1024L // 20MB

@Composable
fun SelectFileScreen(
    selectedFileUri: Uri?,
    onFileSelected: (Uri?) -> Unit,
    toastManager: ToastManager // Assuming passed from parent or via LocalProvider
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileSize = getFileSizeFromUri(context, uri)
            if (fileSize > MAX_FILE_SIZE_BYTES) {
                scope.launch {
                    toastManager.showToast(
                        message = "File size exceeds 20MB limit",
                        icon = Icons.Outlined.ErrorOutline,
                        backgroundColor = Color(0xFFFDECEA),
                        contentColor = Color(0xFFD32F2F)
                    )
                }
                onFileSelected(null)
            } else {
                onFileSelected(uri)
            }
        } else {
            onFileSelected(null)
        }
    }

    Column {
        Text(
            text = "Select Files",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Choose images or PDFs of your paper (Max 20MB)",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium.copy(color = GrayText)
        )

        Spacer(modifier = Modifier.height(32.dp))
        
        if (selectedFileUri == null) {
            UploadBox(filePickerLauncher = filePickerLauncher)
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(LightBlue)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.CloudUpload,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "File selected:",
                        style = MaterialTheme.typography.labelLarge.copy(color = PrimaryBlue)
                    )
                    val fileName = remember(selectedFileUri) {
                        getFileNameFromUri(context, selectedFileUri)
                    }
                    Text(
                        text = fileName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        ),
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { onFileSelected(null) }) {
                        Text("Change File", color = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun UploadBox(filePickerLauncher: ActivityResultLauncher<Array<String>>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .drawBehind {
                val stroke = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
                drawRoundRect(
                    color = DividerColor,
                    style = stroke,
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            }
            .background(Color.Transparent)
            .clickable {
                filePickerLauncher.launch(arrayOf("application/pdf", "image/*"))
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CloudUpload,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = GrayText
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tap to select image or PDF",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBlue
                )
            )
            Text(
                text = "JPG, PNG, PDF supported (Max 20MB)",
                style = MaterialTheme.typography.bodySmall.copy(color = GrayText)
            )
        }
    }
}

fun getFileNameFromUri(context: Context, uri: Uri): String {
    var name = "Unknown file"
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index != -1) name = it.getString(index)
        }
    }
    return name
}

fun getFileSizeFromUri(context: Context, uri: Uri): Long {
    var size = 0L
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val index = it.getColumnIndex(OpenableColumns.SIZE)
            if (index != -1) size = it.getLong(index)
        }
    }
    return size
}
