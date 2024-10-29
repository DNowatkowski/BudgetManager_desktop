package org.example.project.domain.models

import androidx.compose.ui.graphics.vector.ImageVector

data class IconData(
    var id: String = "",
    var name: String = "",
    var image: ImageVector? = null,
)