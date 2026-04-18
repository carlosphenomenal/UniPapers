package com.unipapers.unipapers_frontend.feature.pdfviewer.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.barteksc.pdfviewer.PDFView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    url: String,
    onBackClick: () -> Unit
) {
    BackHandler(onBack = onBackClick)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paper Viewer") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            var isLoading by remember { mutableStateOf(true) }
            var error by remember { mutableStateOf<String?>(null) }
            val coroutineScope = rememberCoroutineScope()

            var isLoaded by remember { mutableStateOf(false) }

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    PDFView(context, null)
                },
                update = { pdfView ->
                    if (url.isNotEmpty() && !isLoaded) {
                        coroutineScope.launch(Dispatchers.IO) {
                            try {
                                val inputStream = URL(url).openStream()
                                withContext(Dispatchers.Main) {
                                    pdfView.fromStream(inputStream)
                                        .onLoad { 
                                            isLoading = false
                                            isLoaded = true
                                        }
                                        .onError { e -> 
                                            error = e.message
                                            isLoading = false
                                        }
                                        .load()
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    error = e.message
                                    isLoading = false
                                }
                            }
                        }
                        isLoaded = true // Mark as loading started
                    }
                }
            )

            if (isLoading) {
                CircularProgressIndicator()
            }

            error?.let {
                Text(text = "Error: $it", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
