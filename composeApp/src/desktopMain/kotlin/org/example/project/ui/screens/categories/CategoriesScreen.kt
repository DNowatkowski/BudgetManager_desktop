package org.example.project.ui.screens.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
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
import budgetmanager.composeapp.generated.resources.Res
import budgetmanager.composeapp.generated.resources.add_group
import budgetmanager.composeapp.generated.resources.group_name
import budgetmanager.composeapp.generated.resources.income
import budgetmanager.composeapp.generated.resources.remove_group
import budgetmanager.composeapp.generated.resources.remove_group_confirmation
import budgetmanager.composeapp.generated.resources.unassigned
import cafe.adriel.voyager.core.screen.Screen
import org.example.project.constants.moneyGreen
import org.example.project.domain.models.toReadableString
import org.example.project.ui.components.dialogs.AlertDialog
import org.example.project.ui.components.dialogs.InputDialog
import org.example.project.ui.components.table.CategoriesHeaderRow
import org.example.project.ui.components.table.GroupRow
import org.jetbrains.compose.resources.stringResource
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
        val iconsState by vm.iconsState.collectAsStateWithLifecycle()

        var showDialog by remember { mutableStateOf(false) }
        var showAlertDialog by remember { mutableStateOf(false) }
        val scrollState = rememberScrollState(0)

        if (showDialog) {
            InputDialog(
                title = stringResource(Res.string.add_group),
                onConfirmed = { text -> vm.addGroup(text) },
                onDismiss = { showDialog = false },
                label = stringResource(Res.string.group_name)
            )
        }

        LaunchedEffect(activeMonth) {
            vm.getDataForMonth(activeMonth)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                TextButton(
                    onClick = { showDialog = true }
                ) {
                    Icon(Icons.Filled.AddCircle, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(Res.string.add_group))
                }
                Spacer(modifier = Modifier.weight(1f))
                TotalCard(uiState)
            }
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        MaterialTheme.shapes.small
                    )
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                var allExpanded by remember { mutableStateOf(false) }
                CategoriesHeaderRow(
                    allExpanded = allExpanded,
                    onToggleAllExpanded = { allExpanded = it }
                )
                Box {
                    Column(modifier = Modifier.verticalScroll(scrollState)) {
                        uiState.categoryGroupsWithKeywords.forEach { group ->
                            var groupExpanded by remember(allExpanded) {
                                if (allExpanded) mutableStateOf(true)
                                else mutableStateOf(false)
                            }
                            if (showAlertDialog)
                                AlertDialog(
                                    title = stringResource(Res.string.remove_group),
                                    text = stringResource(Res.string.remove_group_confirmation),
                                    onDismiss = { showAlertDialog = false },
                                    onConfirmed = { vm.removeGroup(groupId = group.group.id) }
                                )

                            GroupRow(
                                group = group,
                                spending = uiState.groupSpending[group.group] ?: 0.0,
                                target = uiState.groupTargets[group.group] ?: 0.0,
                                onIconClick = {
                                    vm.setIconForGroup(
                                        groupId = group.group.id,
                                        it
                                    )
                                },
                                onSearchTextUpdated = { vm.updateSearchText(it) },
                                icons = iconsState.icons,
                                iconsLoading = iconsState.isLoading,
                                iconsSearchText = iconsState.searchText,
                                expanded = groupExpanded,
                                onGroupUpdated = { id, name ->
                                    vm.updateGroup(
                                        groupId = id,
                                        name = name
                                    )
                                },
                                leadingContent = {
                                    Icon(
                                        imageVector = if (groupExpanded)
                                            Icons.Filled.KeyboardArrowUp
                                        else
                                            Icons.Filled.KeyboardArrowDown,
                                        null,
                                        tint = LocalContentColor.current.copy(alpha = 0.6f)
                                    )
                                },
                                onCategoryAdded = { groupId, name ->
                                    vm.addCategory(
                                        groupId = groupId,
                                        name = name
                                    )
                                },
                                onGroupRemoved = { showAlertDialog = true },
                                modifier = Modifier.clickable {
                                    groupExpanded = !groupExpanded
                                },
                                activeMonth = activeMonth,
                                categorySpending = uiState.categorySpending,
                                onKeywordRemoved = { vm.removeKeyword(it) },
                                onCategoryRemoved = { vm.removeCategory(it) },
                                onKeywordAdded = { categoryId, keyword ->
                                    vm.addKeyword(
                                        text = keyword,
                                        categoryId = categoryId
                                    )
                                },
                                onKeywordUpdated = { vm.updateKeyword(it) },
                                onCategoryUpdated = { categoryId, name ->
                                    vm.updateCategory(
                                        categoryId = categoryId,
                                        name = name
                                    )
                                },
                                onMonthlyTargetSet = { categoryId, target ->
                                    vm.setMonthlyTarget(
                                        categoryId = categoryId,
                                        target = target
                                    )
                                }
                            )
                        }
                    }
                  //  TODO("Add vertical scrollbar")
                }
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
            .clip(MaterialTheme.shapes.small)
            .background(moneyGreen.copy(alpha = 0.1f))
            .border(2.dp, moneyGreen.copy(alpha = 0.6f), MaterialTheme.shapes.small)
            .padding(vertical = 8.dp, horizontal = 16.dp)

    ) {
        val incomeGroup by remember(uiState.groupTargets) { mutableStateOf(uiState.groupTargets.keys.find { it.isIncomeGroup }) }
        Text(
            text = "${stringResource(Res.string.income)}: ${
                uiState.groupTargets[incomeGroup]?.toReadableString(
                    true
                )
            }",
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
            "${stringResource(Res.string.unassigned)}: $unassignedFunds",
            style = MaterialTheme.typography.bodyMedium,
            color = moneyGreen.copy(alpha = 0.7f)
        )
    }
}