package org.example.project.utils

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

fun generateRandomColor(): Color {
    val hueStep = 20
    val randomInt = Random.nextInt(0, hueStep - 1)
    val hue = Random.nextInt(0, (360 / hueStep) + (hueStep * randomInt))


    val saturation = 65

    val brightnessStep = 20
    val randomBrightness = Random.nextInt(0, 2)
    val lightness = Random.nextInt(35, 35 + brightnessStep * randomBrightness)

    val color = Color.hsl(
        hue = hue.toFloat(),
        saturation = saturation.toFloat() / 100,
        lightness = lightness.toFloat() / 100,
    )
    return color
}