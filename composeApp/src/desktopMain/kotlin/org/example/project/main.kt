package org.example.project

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.example.project.di.initKoin

fun main() {
    initKoin(enableNetworkLogs = true)
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Budget Manager",
            state = rememberWindowState(
                size = DpSize(width = 1400.dp, height = 900.dp),
                position = WindowPosition(Alignment.Center)
            ),
        ) {
            App()
        }
    }
}