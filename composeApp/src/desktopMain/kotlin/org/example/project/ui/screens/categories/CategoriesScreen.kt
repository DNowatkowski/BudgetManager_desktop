package org.example.project.ui.screens.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import org.example.project.constants.moneyGreen
import org.example.project.domain.models.toReadableString
import org.example.project.ui.components.VerticalScrollBar
import org.example.project.ui.components.dialogs.AlertDialog
import org.example.project.ui.components.dialogs.InputDialog
import org.example.project.ui.components.table.CategoriesHeaderRow
import org.example.project.ui.components.table.CategoryRow
import org.example.project.ui.components.table.GroupRow
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import java.time.LocalDate
import kotlin.math.absoluteValue


data class CategoriesScreen(
    val activeMonth: LocalDate,
) : Screen {
    @OptIn(KoinExperimentalAPI::class)
    @Composable
    override fun Content() {
        val vm = koinViewModel<CategoriesScreenViewModel>()
        val uiState by vm.uiState.collectAsStateWithLifecycle()
        var showDialog by remember { mutableStateOf(false) }
        var showAlertDialog by remember { mutableStateOf(false) }
        val scrollState = rememberScrollState(0)

        if (showDialog) {
            InputDialog(
                title = "Add group",
                onConfirmed = { text -> vm.addGroup(text) },
                onDismiss = { showDialog = false },
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
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    TextButton(
                        onClick = { showDialog = true }
                    ) {
                        Icon(Icons.Filled.AddCircle, null)
                        Text(" Add group")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TotalCard(uiState)
                }
                CategoriesHeaderRow()

                Box{
                    Column(
                        modifier = Modifier.verticalScroll(scrollState)
                    ) {
                        uiState.categoryGroupsWithKeywords.forEach { group ->

                            if (showAlertDialog)
                                AlertDialog(
                                    title = "Remove group",
                                    text = "Are you sure you want to remove this group and all it's categories?",
                                    onDismiss = { showAlertDialog = false },
                                    onConfirmed = { vm.removeGroup(groupId = group.group.id) }
                                )

                            GroupRow(
                                group = group,
                                spending = uiState.groupSpending[group.group] ?: 0.0,
                                target = uiState.groupTargets[group.group] ?: 0.0,
                                onGroupUpdated = { id, name ->
                                    vm.updateGroup(
                                        groupId = id,
                                        name = name
                                    )
                                },
                                onCategoryAdded = { groupId, name ->
                                    vm.addCategory(
                                        groupId = groupId,
                                        name = name
                                    )
                                },
                                onGroupRemoved = { showAlertDialog = true }
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
                                    groupColor = group.group.color,
                                    activeMonth = activeMonth,
                                    spending = uiState.categorySpending[category.category] ?: 0.0
                                )
                            }
                        }
                    }
                    VerticalScrollBar(
                        scrollState = scrollState,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }

    }
}


@Composable
fun TotalCard(
    uiState: CategoriesScreenViewModel.CategoriesState
) {
    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.large)
            .background(moneyGreen.copy(alpha = 0.1f))
            .border(2.dp, moneyGreen.copy(alpha = 0.6f), MaterialTheme.shapes.large)
            .padding(vertical = 8.dp, horizontal = 16.dp)

    ) {
        val incomeGroup by remember(uiState.groupTargets) { mutableStateOf(uiState.groupTargets.keys.find { it.isIncomeGroup }) }
        Text(
            text = "Income: ${uiState.groupTargets[incomeGroup]?.toReadableString(true)}",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight(30)),
            color = moneyGreen
        )
        val unassignedFunds by remember(uiState.groupTargets) {
            val incomeGroupTarget = uiState.groupTargets[incomeGroup] ?: 0.0
            val otherGroupTargetsSum =
                uiState.groupTargets.filterKeys { it != incomeGroup }.values.sum().absoluteValue
            mutableStateOf(
                (incomeGroupTarget - otherGroupTargetsSum).toReadableString(
                    true
                )
            )
        }
        Text(
            "Unassigned: $unassignedFunds",
            style = MaterialTheme.typography.bodyMedium,
            color = moneyGreen.copy(alpha = 0.7f)
        )
    }
}