package com.unipapers.unipapers_frontend.feature.filemanagement.utils

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

/**
 * Computes the SHA-256 hash of a file.
 *
 * The hash is computed by reading the file in chunks, which keeps memory usage
 * low even for large PDF files.
 *
 * @return Lowercase hex-encoded SHA-256 digest string (64 characters),
 *         or null if the file does not exist or cannot be read.
 */
object FileHashUtil {

    private const val BUFFER_SIZE = 8 * 1024 // 8 KB chunks

    fun sha256(file: File): String? {
        if (!file.exists() || !file.canRead()) return null

        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            FileInputStream(file).use { inputStream ->
                val buffer = ByteArray(BUFFER_SIZE)
                var bytesRead = inputStream.read(buffer)
                while (bytesRead != -1) {
                    digest.update(buffer, 0, bytesRead)
                    bytesRead = inputStream.read(buffer)
                }
            }
            digest.digest().toHexString()
        } catch (_: Exception) {
            null
        }
    }

    private fun ByteArray.toHexString(): String =
        joinToString(separator = "") { byte -> "%02x".format(byte) }
}