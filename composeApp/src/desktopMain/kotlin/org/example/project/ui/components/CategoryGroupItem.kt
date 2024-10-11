package org.example.project.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.domain.models.category.CategoryWithKeywords
import org.example.project.domain.models.keyword.KeywordData

@Composable
fun CategoryGroupItem(
    title: String,
    categories: List<CategoryWithKeywords>,
    onAddCategory: (String) -> Unit,
    onUpdateGroup: (String) -> Unit,
    onKeywordUpdated: (KeywordData) -> Unit,
    onAddKeyword: (String, String) -> Unit,
    onRemoveKeyword: (KeywordData) -> Unit,
    onRemoveGroup: () -> Unit,
    onKeywordDropped: (keywordId: String, newCategoryId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showEditGroupDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    if (showAddCategoryDialog) {
        InputDialog(
            title = "Add Category",
            onConfirmed = onAddCategory,
            onDismiss = { showAddCategoryDialog = false },
            label = "Category",
        )
    }

    if (showEditGroupDialog) {
        InputDialog(
            title = "Edit Group",
            initialText = title,
            onConfirmed = onUpdateGroup,
            onDismiss = { showEditGroupDialog = false },
            label = "Group",
        )
    }
    Card(modifier = modifier) {
        Column(Modifier.padding(vertical = 8.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Text(style = MaterialTheme.typography.headlineSmall, text = title)
                Box {
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        DropdownMenuItem(onClick = { onRemoveGroup() }) {
                            Icon(Icons.Filled.Delete, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Remove")
                        }
                        DropdownMenuItem(
                            onClick = {
                                expanded = false
                                showEditGroupDialog = true
                            }) {
                            Icon(Icons.Filled.Edit, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Edit")
                        }
                        DropdownMenuItem(
                            onClick = {
                                expanded = false
                                showAddCategoryDialog = true
                            }) {
                            Icon(Icons.Filled.Add, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Add Category")
                        }
                    }
                    IconButton(
                        onClick = { expanded = true }
                    ) {
                        Icon(Icons.Filled.MoreVert, null)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            categories.forEach { category ->
                CategoryItem(
                    title = category.name,
                    keywords = category.keywords,
                    onAddKeyword = { keyword -> onAddKeyword(keyword, category.id) },
                    onKeywordUpdated = onKeywordUpdated,
                    onRemoveKeyword = onRemoveKeyword,
                    onKeywordDropped = { keywordId ->
                        onKeywordDropped(keywordId, category.id) }
                )
            }
        }
    }
}
