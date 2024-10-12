package org.example.project.ui.screens.keywords

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import org.example.project.ui.components.CategoryGroupItem
import org.example.project.ui.components.InputDialog
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI


class KeywordsScreen : Screen {
    @OptIn(KoinExperimentalAPI::class)
    @Composable
    override fun Content() {
        val vm = koinViewModel<KeywordsScreenViewModel>()
        val uiState by vm.uiState.collectAsStateWithLifecycle()
        val listState = rememberLazyListState(0)

        var showDialog by remember { mutableStateOf(false) }

        if (showDialog) {
            InputDialog(
                title = "Add group",
                onConfirmed = { text -> vm.addGroup(text) },
                onDismiss = { showDialog = false },
                label = "Group name"
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                TextButton(
                    onClick = {
                        showDialog = true
                    }
                ) {
                    Icon(Icons.Filled.AddCircle, null)
                    Text(" Add group")
                }
            }

            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(uiState.categoryGroups.size) { index ->
                    val group = uiState.categoryGroups[index]
                    CategoryGroupItem(
                        title = group.name,
                        categories = group.categories,
                        onAddCategory = { text -> vm.addCategory(text, group.id) },
                        onAddKeyword = { text, categoryId -> vm.addKeyword(text, categoryId) },
                        onRemoveKeyword = { id -> vm.removeKeyword(id) },
                        onUpdateGroup = { text -> vm.updateGroup(text, group.id) },
                        onRemoveGroup = { vm.removeGroup(group.id) },
                        onKeywordUpdated = { keyword -> vm.updateKeyword(keyword) },
                        onKeywordDropped = { keywordId, newCategoryId ->
                            vm.moveKeyword(
                                keywordId = keywordId,
                                newCategoryId = newCategoryId
                            )
                        },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}

