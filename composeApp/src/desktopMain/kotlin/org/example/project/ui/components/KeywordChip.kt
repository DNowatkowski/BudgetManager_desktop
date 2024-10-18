package org.example.project.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTransferAction
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.DragAndDropTransferable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import org.example.project.domain.models.keyword.KeywordData
import java.awt.datatransfer.StringSelection

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun KeywordChip(
    keyword: KeywordData,
    onRemoveKeyword: (KeywordData) -> Unit,
    onKeywordUpdated: (KeywordData) -> Unit,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog)
        InputDialog(
            title = "Edit Keyword",
            initialText = keyword.keyword,
            onConfirmed = { onKeywordUpdated(keyword.copy(keyword = it)) },
            onDismiss = { showDialog = false },
            label = "Keyword",
        )
    Box {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(onClick = { onRemoveKeyword(keyword) }) {
                Icon(Icons.Filled.Delete, null)
                Spacer(Modifier.width(8.dp))
                Text("Remove")
            }
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    showDialog = true
                }) {
                Icon(Icons.Filled.Edit, null)
                Spacer(Modifier.width(8.dp))
                Text("Edit")
            }
        }
        InputChip(
            selected = false,
            label = { Text(keyword.keyword, style = MaterialTheme.typography.bodySmall) },
            onClick = { expanded = true },
            modifier = modifier.dragAndDropSource(
                // Creates a visual representation of the data being dragged
                // (white rectangle with the exportedText string centered on it).
                drawDragDecoration = {
                    val textLayoutResult = textMeasurer.measure(
                        text = AnnotatedString(keyword.keyword),
                        layoutDirection = layoutDirection,
                        density = this
                    )

                    drawRect(
                        color = Color.White.copy(alpha = 0.5f),
                        topLeft = Offset(x = 0f, y = size.height / 4),
                        size = Size(size.width, size.height / 2)
                    )

                    drawText(
                        textLayoutResult = textLayoutResult,
                        topLeft = Offset(
                            x = (size.width - textLayoutResult.size.width) / 2,
                            y = (size.height - textLayoutResult.size.height) / 2,
                        )
                    )
                }
            ) {
                detectDragGestures(
                    onDragStart = { offset ->
                        startTransfer(
                            // Defines transferable data and supported transfer actions.
                            // When an action is concluded, prints the result into
                            // system output with onTransferCompleted().
                            DragAndDropTransferData(
                                transferable = DragAndDropTransferable(
                                    StringSelection(keyword.id)
                                ),

                                // List of actions supported by this drag source. A type of action
                                // is passed to the drop target together with data.
                                // The target can use this to reject an inappropriate drop operation
                                // or to interpret user expectations.
                                supportedActions = listOf(
                                    DragAndDropTransferAction.Copy,
                                    DragAndDropTransferAction.Move,
                                    DragAndDropTransferAction.Link,
                                ),
                                dragDecorationOffset = offset,
                                onTransferCompleted = { action ->
                                    println("Action at the source: $action")
                                }
                            )
                        )
                    },
                    onDrag = { _, _ -> },
                )
            }
        )
    }
}