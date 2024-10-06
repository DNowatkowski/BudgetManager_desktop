package org.example.project.ui.screens.keywords

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import org.example.project.ui.components.CategoryGroupItem
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

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(uiState.categoryGroups.size) { index ->
                val group = uiState.categoryGroups[index]
                CategoryGroupItem(
                    title = group.name,
                    categories = group.categories,
                    onAddCategory = { text -> vm.addCategory(text, group.id) },
                    onAddKeyword = { text, categoryId -> vm.addKeyword(text, categoryId) },
                    onRemoveKeyword = { id -> vm.removeKeyword(id) },
                    onAddGroup = {}
                )
            }
        }
    }
}

