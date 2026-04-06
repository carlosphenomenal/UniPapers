package com.unipapers.unipapers_frontend.feature.filemanagement.receiver

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
import com.unipapers.unipapers_frontend.feature.filemanagement.domain.repository.FileRepository

/**
 * A [BroadcastReceiver] that listens for [DownloadManager.ACTION_DOWNLOAD_COMPLETE] broadcasts.
 *
 * This receiver is responsible for notifying the [FileRepository] when a download finishes,
 * allowing the repository to refresh the local state and progress of the downloaded file.
 *
 * It uses [goAsync] to perform the repository sync in a background coroutine since
 * [onReceive] runs on the main thread and has a short timeout.
 */
@AndroidEntryPoint
class DownloadCompleteReceiver : BroadcastReceiver() {

    @Inject
    lateinit var fileRepository: FileRepository

    /**
     * Called when the [DownloadManager] finishes a download.
     *
     * Processes the [DownloadManager.EXTRA_DOWNLOAD_ID] and triggers an asynchronous
     * sync via [FileRepository.syncDownloadedFile].
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        // Only handle download complete actions
        if (intent?.action != DownloadManager.ACTION_DOWNLOAD_COMPLETE) return

        val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
        if (downloadId <= 0L) return

        // goAsync() allows the receiver to stay alive for up to 10 seconds while
        // we perform background work.
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                // Sync the specific download status in the repository
                fileRepository.syncDownloadedFile(downloadId)
            } finally {
                // Signals to the system that the receiver can now be terminated
                pendingResult.finish()
            }
        }
    }
}

