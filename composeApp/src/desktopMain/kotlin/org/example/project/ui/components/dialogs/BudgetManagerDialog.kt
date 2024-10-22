package org.example.project.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog


@Composable
fun BudgetManagerDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirmed: () -> Unit,
    confirmEnabled: Boolean = true,
    confirmButtonText: String,
    dismissButtonText: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Box(
            modifier = modifier
                .wrapContentHeight()
                .wrapContentWidth()
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                .padding(horizontal = 24.dp, vertical = 16.dp)

        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.TopCenter)
            )
            Column(
                modifier = Modifier.align(Alignment.Center).padding(top = 48.dp, bottom = 58.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                content()
            }
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Button(
                    enabled = confirmEnabled,
                    onClick = {
                        onConfirmed()
                        onDismiss()
                    }) {
                    Text(confirmButtonText)
                }
                Spacer(Modifier.width(8.dp))
                OutlinedButton(onClick = onDismiss) {
                    Text(dismissButtonText)
                }
            }
        }
    }
}