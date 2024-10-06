package org.example.project.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.domain.models.CategoryData
import org.example.project.domain.models.KeywordData

@Composable
fun CategoryGroupItem(
    title: String,
    categories: List<CategoryData>,
    onAddGroup: (String) -> Unit,
    onAddCategory: (String) -> Unit,
    onAddKeyword: (String, String) -> Unit,
    onRemoveKeyword: (KeywordData) -> Unit,
) {
    Card {
        Column(Modifier.padding(16.dp)) {
            Text(style = MaterialTheme.typography.headlineSmall, text = title)
            HorizontalDivider()
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                categories.forEach {
                    CategoryItem(
                        title = it.name,
                        keywords = it.keywords,
                        onAddKeyword = { keyword -> onAddKeyword(keyword, it.id) },
                        onRemoveKeyword = onRemoveKeyword,
                    )
                }
                Card(elevation = CardDefaults.cardElevation(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {

                        }
                ) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.AddCircle, null, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryItem(
    title: String,
    keywords: List<KeywordData>,
    onAddKeyword: (String) -> Unit,
    onRemoveKeyword: (KeywordData) -> Unit,
) {
    Card(elevation = CardDefaults.cardElevation(6.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("$title:")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                keywords.forEach {
                    KeywordChip(
                        keyword = it,
                        onRemoveKeyword = onRemoveKeyword,
                    )
                }
                AddKeywordChip(
                    onAddKeyword = onAddKeyword,
                )
            }
        }
    }
}

@Composable
fun KeywordChip(
    keyword: KeywordData,
    onRemoveKeyword: (KeywordData) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
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
            DropdownMenuItem(onClick = { expanded = false }) {
                Icon(Icons.Filled.Edit, null)
                Spacer(Modifier.width(8.dp))
                Text("Edit")
            }
        }
        InputChip(
            selected = false,
            label = { Text(keyword.keyword) },
            onClick = { expanded = true },
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddKeywordChip(
    onAddKeyword: (String) -> Unit,
) {
    var editMode by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }

    InputChip(
        selected = false,
        label = {
            AnimatedContent(targetState = editMode) {
                if (editMode) {
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        singleLine = true,
                        modifier = Modifier
                    )
                } else {
                    Icon(Icons.Filled.Add, null)
                }
            }


        },
        onClick = { editMode = true },
    )
}