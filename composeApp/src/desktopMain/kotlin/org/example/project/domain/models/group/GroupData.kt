package org.example.project.domain.models.group

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class GroupData(
    val id: String,
    val color: Color,
    val name: String,
    val icon: ImageVector?,
    val isIncomeGroup: Boolean,
)
