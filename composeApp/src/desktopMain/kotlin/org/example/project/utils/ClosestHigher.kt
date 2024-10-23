package org.example.project.utils

fun closestHigherMultipleOf1000(number: Double?): Double {
    return if (number != null) {
        ((number / 1000).toInt() * 1000 + 1000.0)
    } else
        1000.0
}
