package org.example.project

import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.example.project.di.initKoin

fun main() {
    initKoin(enableNetworkLogs = true)
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "BudgetManager",
            state = rememberWindowState(
                size = DpSize(width = 1400.dp, height = 800.dp),
                position = WindowPosition(Alignment.Center)
            ),
        ) {
            App()
        }
    }
}