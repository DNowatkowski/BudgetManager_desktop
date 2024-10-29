package org.example.project.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowCircleLeft
import androidx.compose.material.icons.outlined.ArrowCircleRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DateSwitcher(
    activeMonth: LocalDate,
    onNextMonth: () -> Unit,
    onPreviousMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(true) }
    var monthText by remember { mutableStateOf(activeMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())) }
    var slideDirection by remember { mutableStateOf(1) } // 1 for next, -1 for previous
    var isFirstDisplay by remember { mutableStateOf(true) }

    LaunchedEffect(activeMonth) {
        if (!isFirstDisplay) {
            visible = false
            delay(100)
            monthText = activeMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
            visible = true
        } else {
            monthText = activeMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
            isFirstDisplay = false
        }
    }


    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = modifier) {
        IconButton(onClick = {
            slideDirection = -1
            onPreviousMonth()
        }) {
            Icon(
                Icons.Outlined.ArrowCircleLeft,
                null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(35.dp)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.widthIn(130.dp)
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = slideInHorizontally(initialOffsetX = { slideDirection * it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { -slideDirection * it }) + fadeOut()
            ) {
                val monthCapitalized = monthText.lowercase()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                Text(monthCapitalized, style = MaterialTheme.typography.headlineSmall)
            }
            Text(activeMonth.year.toString(), style = MaterialTheme.typography.labelMedium)
        }
        IconButton(onClick = {
            slideDirection = 1
            onNextMonth()
        }) {
            Icon(
                Icons.Outlined.ArrowCircleRight,
                null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(35.dp)
            )
        }
    }
}