package org.example.project.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.awtTransferable
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.unit.dp
import org.example.project.domain.models.KeywordData
import java.awt.datatransfer.DataFlavor

@OptIn(
    ExperimentalLayoutApi::class, ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class, ExperimentalTextApi::class, ExperimentalAnimationApi::class
)
@Composable
fun CategoryItem(
    title: String,
    keywords: List<KeywordData>,
    onAddKeyword: (String) -> Unit,
    onKeywordUpdated: (KeywordData) -> Unit,
    onRemoveKeyword: (KeywordData) -> Unit,
    onKeywordDropped: (String) -> Unit,
) {
    val listState = rememberLazyListState()
    var showTargetBorder by remember { mutableStateOf(false) }
    val dragAndDropTarget = remember {
        object : DragAndDropTarget {

            // Highlights the border of a potential drop target
            override fun onStarted(event: DragAndDropEvent) {
                showTargetBorder = true
            }

            override fun onEnded(event: DragAndDropEvent) {
                showTargetBorder = false
            }

            override fun onDrop(event: DragAndDropEvent): Boolean {
                val id = event.awtTransferable.let {
                    if (it.isDataFlavorSupported(DataFlavor.stringFlavor))
                        it.getTransferData(DataFlavor.stringFlavor) as String
                    else
                        it.transferDataFlavors.first().humanPresentableName
                }
                onKeywordDropped(id)
                return true
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .then(
                if (showTargetBorder)
                    Modifier
                        .border(
                            BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                            shape = MaterialTheme.shapes.medium
                        )
                else
                    Modifier
            )
            .dragAndDropTarget(
                // With "true" as the value of shouldStartDragAndDrop,
                // drag-and-drop operations are enabled unconditionally.
                shouldStartDragAndDrop = { true },
                target = dragAndDropTarget
            )
    ) {
        Text("$title:", modifier = Modifier.padding(start = 8.dp))
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(count = keywords.size, key = { keywords[it].id }) { index ->
                val keyword = keywords[index]
                KeywordChip(
                    keyword = keyword,
                    onRemoveKeyword = onRemoveKeyword,
                    onKeywordUpdated = onKeywordUpdated,
                    modifier = Modifier.animateItem()
                )
            }
            item {
                AddKeywordChip(
                    onAddKeyword = onAddKeyword,
                )
            }
        }
    }
}