package com.unipapers.unipapers_frontend.feature.upload.receiver

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.unipapers.unipapers_frontend.feature.upload.domain.repository.FileRepository

@AndroidEntryPoint
class DownloadCompleteReceiver : BroadcastReceiver() {

    @Inject
    lateinit var fileRepository: FileRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != DownloadManager.ACTION_DOWNLOAD_COMPLETE) return

        val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
        if (downloadId <= 0L) return

        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                fileRepository.syncDownloadedFile(downloadId)
            } finally {
                pendingResult.finish()
            }
        }
    }
}

