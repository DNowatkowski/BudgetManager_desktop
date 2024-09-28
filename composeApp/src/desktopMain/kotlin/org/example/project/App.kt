package org.example.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.example.project.ui.screens.MainScreen
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    MaterialTheme {
        MainScreen()
    }
}