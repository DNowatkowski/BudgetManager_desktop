package org.example.project.utils

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.vector.ImageVector


object ImageUtil {
    fun createImageVector(name: String): ImageVector? {
        try {
            val className = "androidx.compose.material.icons.filled.${name}Kt"
            val cl = Class.forName(className)
            val method = cl.declaredMethods.first()
            return method.invoke(null, Icons.Outlined) as ImageVector
        } catch (ex: Exception) {
            println(ex)
            return null
        }
    }
}