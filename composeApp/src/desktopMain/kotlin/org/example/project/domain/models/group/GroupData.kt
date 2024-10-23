package org.example.project.domain.models.group

import androidx.compose.ui.graphics.Color

data class GroupData(
    val id: String,
    val color: Color,
    val name: String,
    val isIncomeGroup: Boolean,
)
