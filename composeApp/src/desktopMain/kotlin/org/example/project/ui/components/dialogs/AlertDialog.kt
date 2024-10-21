package org.example.project.ui.components.dialogs

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AlertDialog(
    title: String,
    text:String,
    onDismiss: () -> Unit,
    onConfirmed: () -> Unit,
){
    BudgetManagerDialog(
        title = title,
        onDismiss = { onDismiss() },
        onConfirmed = onConfirmed,
        confirmButtonText = "Delete",
        dismissButtonText = "Cancel",
        content = {
            Icon(
                Icons.Outlined.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    )
}