package org.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog


@Composable
fun EditKeywordDialog(
    keywordText: String = "",
    onConfirmed: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf(keywordText) }
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                .padding(16.dp)
                .width(400.dp)
        ) {
            Text("Add keyword", style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Keyword") },
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    enabled = text.isNotBlank(),
                    onClick = {
                        onConfirmed(text)
                        onDismiss()
                    }) {
                    Text("Add")
                }
                Spacer(Modifier.width(8.dp))
                OutlinedButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    }
}