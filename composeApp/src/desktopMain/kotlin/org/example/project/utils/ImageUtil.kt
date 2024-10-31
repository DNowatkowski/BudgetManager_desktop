package org.example.project.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.ui.graphics.vector.ImageVector


object ImageUtil {
    fun createImageVector(name: String): ImageVector? {
        try {
            Icons.Filled.AddCircle
            val className = "androidx.compose.material.icons.filled.${name}Kt"
            val cl = Class.forName(className)
            val method = cl.declaredMethods.first()
            return method.invoke(null, Icons.Filled) as ImageVector
        } catch (ex: Exception) {
            println(ex)
            return null
        }
    }
}