package org.example.project.utils

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

fun generateRandomColor(): Color {
    val hue = Random.nextInt(0, 360) // Random hue between 0 and 360
    val saturation = Random.nextInt(35, 65) // Low saturation for pastel colors
    val lightness = Random.nextInt(65, 80) // High lightness for bright colors

    val color = Color.hsl(
        hue = hue.toFloat(),
        saturation = saturation.toFloat() / 100,
        lightness = lightness.toFloat() / 100,
    )
    return color
}