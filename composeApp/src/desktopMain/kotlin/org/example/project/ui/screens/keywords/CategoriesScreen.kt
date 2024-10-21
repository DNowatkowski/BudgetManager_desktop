package org.example.project.ui.screens.keywords

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import org.example.project.ui.components.dialogs.InputDialog
import org.example.project.ui.components.table.CategoriesHeaderRow
import org.example.project.ui.components.table.CategoryRow
import org.example.project.ui.components.table.GroupRow
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import java.time.LocalDate


data class CategoriesScreen(
    val activeMonth: LocalDate,
) : Screen {
    @OptIn(KoinExperimentalAPI::class)
    @Composable
    override fun Content() {
        val vm = koinViewModel<CategoriesScreenViewModel>()
        val uiState by vm.uiState.collectAsStateWithLifecycle()
        val showDialog = remember { mutableStateOf(false) }

        if (showDialog.value) {
            InputDialog(
                title = "Add group",
                onConfirmed = { text -> vm.addGroup(text) },
                onDismiss = { showDialog.value = false },
                label = "Group name"
            )
        }

        LaunchedEffect(activeMonth) {
            vm.getDataForMonth(activeMonth)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.background(MaterialTheme.colorScheme.background).fillMaxSize()
        ) {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                TextButton(
                    onClick = { showDialog.value = true }
                ) {
                    Icon(Icons.Filled.AddCircle, null)
                    Text(" Add group")
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            CategoriesHeaderRow()
            HorizontalDivider()
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                uiState.categoryGroupsWithKeywords.forEach { group ->
                    GroupRow(
                        group = group,
                        spending = uiState.groupSpending[group.group] ?: 0.0,
                        target = uiState.groupTargets[group.group] ?: 0.0,
                        onGroupUpdated = { id, name -> vm.updateGroup(groupId = id, name = name) },
                        onCategoryAdded = { groupId, name ->
                            vm.addCategory(
                                groupId = groupId,
                                name = name
                            )
                        },
                        onGroupRemoved = { vm.removeGroup(it) }
                    )
                    group.categories.forEach { category ->
                        CategoryRow(
                            onCategoryRemoved = { vm.removeCategory(categoryId = category.category.id) },
                            onCategoryUpdated = { id, name ->
                                vm.updateCategory(
                                    categoryId = id,
                                    name = name
                                )
                            },
                            onKeywordRemoved = { keyword -> vm.removeKeyword(keyword) },
                            onKeywordAdded = { categoryId, text ->
                                vm.addKeyword(
                                    categoryId = categoryId,
                                    text = text
                                )
                            },
                            onKeywordUpdated = { vm.updateKeyword(it) },
                            onMonthlyTargetSet = { categoryId, target ->
                                vm.setMonthlyTarget(
                                    categoryId = categoryId,
                                    target = target
                                )
                            },
                            category = category,
                            activeMonth = activeMonth,
                            spending = uiState.categorySpending[category.category] ?: 0.0
                        )
                    }
                }
            }
        }
    }
}
