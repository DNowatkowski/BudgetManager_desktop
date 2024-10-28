package org.example.project

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.datetime.LocalDate
import org.example.project.di.initKoin
import org.example.project.utils.generateRandomColor
import kotlin.random.Random

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
            repeat(17) { index ->
                val hueStep = 20
                val randomInt = Random.nextInt(0, 18)
                val hue = hueStep * index

                val saturation = 65

                val brightnessStep = 25
                val randomBrightness = Random.nextInt(2, 5)
                val lightness = brightnessStep * 2

                val color = Color.hsl(
                    hue = hue.toFloat(),
                    saturation = saturation.toFloat() / 100,
                    lightness = lightness.toFloat() / 100,
                )
                println(color.toArgb().toDouble())

            }
            App()
        }
    }
}