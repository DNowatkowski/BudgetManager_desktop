package org.example.project.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier
                .sizeIn(minWidth = 150.dp, minHeight = 100.dp)
                .wrapContentSize()
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)

        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge
            )
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            content()
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
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