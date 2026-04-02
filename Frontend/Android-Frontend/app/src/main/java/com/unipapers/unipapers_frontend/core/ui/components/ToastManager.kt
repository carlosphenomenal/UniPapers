package com.unipapers.unipapers_frontend.core.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

data class ToastData(
    val message: String,
    val icon: ImageVector? = null,
    val backgroundColor: Color? = null,
    val contentColor: Color? = null,
    val duration: Long = 3000L
)

@Singleton
class ToastManager @Inject constructor() {
    private val _toastEvents = MutableSharedFlow<ToastData>()
    val toastEvents = _toastEvents.asSharedFlow()

    suspend fun showToast(
        message: String,
        icon: ImageVector? = null,
        backgroundColor: Color? = null,
        contentColor: Color? = null,
        duration: Long = 3000L
    ) {
        _toastEvents.emit(ToastData(message, icon, backgroundColor, contentColor, duration))
    }
}
