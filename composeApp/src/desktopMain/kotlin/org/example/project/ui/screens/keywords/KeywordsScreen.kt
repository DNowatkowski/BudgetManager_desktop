package org.example.project.ui.screens.keywords

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import database.KeywordEntity
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI


class KeywordsScreen : Screen {
    @OptIn(KoinExperimentalAPI::class)
    @Composable
    override fun Content() {
        val vm = koinViewModel<KeywordsScreenViewModel>()
        val uiState by vm.uiState.collectAsStateWithLifecycle()

        val launcher = rememberFilePickerLauncher(mode = PickerMode.Single) { file ->
            vm.importFile(file)
        }

        Button(onClick = {
            launcher.launch()
        }) {
            Text("Import file")
        }

        LazyColumn {
            items(uiState.categoriesWithKeywords.size) { index ->
                val category = uiState.categoriesWithKeywords[index]
                CategoryItem(
                    title = category.category.name,
                    keywords = category.keywords,
                    onAddCategory = { text -> vm.addCategory(text) },
                    onAddKeyword = { text -> vm.addKeyword(text, category.category.id) },
                    onRemoveKeyword = { keyword -> vm.removeKeyword(keyword) },
                )
            }
        }

    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterialApi::class)
@Composable
fun CategoryItem(
    title: String,
    keywords: List<KeywordEntity>,
    onAddCategory: (String) -> Unit,
    onAddKeyword: (String) -> Unit,
    onRemoveKeyword: (KeywordEntity) -> Unit,
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(title)
            IconButton(onClick = { onAddCategory("New category") }) {
                Icon(Icons.Filled.Add, null)
            }
        }
        FlowRow {
            keywords.forEach {
                Chip(
                    leadingIcon = {
                        IconButton(onClick = { onRemoveKeyword(it) }) {
                            Icon(Icons.Filled.Delete, null)
                        }
                    },
                    onClick = { },
                ) {
                    Text(it.keyword)
                }
            }
            Chip(
                onClick = { onAddKeyword("New keyword") },
            ) {
                Text("Add keyword")
            }
        }
    }
}