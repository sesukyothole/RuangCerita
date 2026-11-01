package com.example.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

@Composable
fun StoryPinDialog(
    isDark: Boolean = isSystemInDarkTheme(),
    onDismiss: () -> Unit,
    onPinCorrect: () -> Unit,
    onOpenAdminChangePin: () -> Unit = {}
) {
    DashboardPinDialog(
        isDark = isDark,
        onDismiss = onDismiss,
        onPinCorrect = onPinCorrect,
        onOpenAdminChangePin = onOpenAdminChangePin
    )
}
