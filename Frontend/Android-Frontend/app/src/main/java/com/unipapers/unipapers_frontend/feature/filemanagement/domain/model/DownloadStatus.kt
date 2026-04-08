package com.unipapers.unipapers_frontend.feature.filemanagement.domain.model

/**
 * Represents the current status of a download.
 * This acts as the source of truth for the UI and the repository.
 */
enum class DownloadStatus {
    /** The download is waiting to start. */
    PENDING,
    /** The download is currently in progress. */
    DOWNLOADING,
    /** The user has manually paused the download. */
    PAUSED,
    /** The download is waiting for a network connection. */
    WAITING_FOR_NETWORK,
    /** The download has completed successfully. */
    COMPLETED,
    /** The download failed due to an error. */
    FAILED,
    /** The download was cancelled by the user. */
    CANCELLED
}
