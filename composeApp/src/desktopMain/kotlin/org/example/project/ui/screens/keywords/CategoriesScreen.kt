package org.example.project.ui.screens.keywords

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.example.project.constants.CategoryColumn
import org.example.project.domain.models.category.CategoryWithKeywords
import org.example.project.domain.models.group.GroupWithCategoriesAndKeywordsData
import org.example.project.ui.components.CategoryGroupItem
import org.example.project.ui.components.InputDialog
import org.example.project.ui.components.TableCell
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.math.roundToInt


class CategoriesScreen : Screen {
    @OptIn(KoinExperimentalAPI::class)
    @Composable
    override fun Content() {
        val vm = koinViewModel<CategoriesScreenViewModel>()
        val uiState by vm.uiState.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()
        val pagerState = rememberPagerState(pageCount = { 2 })

        val showDialog = remember { mutableStateOf(false) }

        if (showDialog.value) {
            InputDialog(
                title = "Add group",
                onConfirmed = { text -> vm.addGroup(text) },
                onDismiss = { showDialog.value = false },
                label = "Group name"
            )
        }

        Column {
            HeaderRow(pagerState, coroutineScope, showDialog)
            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            HorizontalPager(
                state = pagerState,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> Targets(vm, uiState)
                    1 -> Keywords(vm, uiState)
                }
            }
        }
    }
}

@Composable
private fun HeaderRow(
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    showDialog: MutableState<Boolean>
) {
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        TextButton(
            onClick = { showDialog.value = true }
        ) {
            Icon(Icons.Filled.AddCircle, null)
            Text(" Add group")
        }
        Spacer(modifier = Modifier.weight(1f))
        SingleChoiceSegmentedButtonRow {
            SegmentedButton(
                selected = pagerState.currentPage == 0,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                shape = SegmentedButtonDefaults.itemShape(0, pagerState.pageCount)
            ) {
                Text("Targets")
            }
            SegmentedButton(
                selected = pagerState.currentPage == 1,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                shape = SegmentedButtonDefaults.itemShape(1, pagerState.pageCount)
            ) {
                Text("Keywords")
            }
        }
    }
}

@Composable
private fun Keywords(
    vm: CategoriesScreenViewModel,
    uiState: CategoriesScreenViewModel.CategoriesState
) {
    val gridState = rememberLazyStaggeredGridState(0)

    LazyVerticalStaggeredGrid(
        state = gridState,
        columns = StaggeredGridCells.Adaptive(500.dp),
        verticalItemSpacing = 16.dp,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(top = 16.dp),
    ) {
        items(uiState.categoryGroupsWithKeywords.size) { index ->
            val group = uiState.categoryGroupsWithKeywords[index]
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

@Composable
private fun Targets(
    vm: CategoriesScreenViewModel,
    uiState: CategoriesScreenViewModel.CategoriesState,
) {
    Column {
        TargetHeaderRow()
        HorizontalDivider()
        uiState.categoryGroupsWithKeywords.forEach { group ->
            GroupRow(vm, group)
            group.categories.forEach { category ->
                CategoryRow(vm, category)
            }
        }
    }
}

@Composable
private fun TargetHeaderRow() {
    Row(
        modifier = Modifier.fillMaxWidth().height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TableCell(weight = CategoryColumn.CATEGORY.weight) {
            Text(
                "Category",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        VerticalDivider()
        TableCell(weight = CategoryColumn.ACTUAL_SPENDING.weight) {
            Text(
                "Actual spending",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
            )
        }
        VerticalDivider()
        TableCell(weight = CategoryColumn.MONTHLY_TARGET.weight) {
            Text(
                "Monthly target",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}

@Composable
private fun GroupRow(vm: CategoriesScreenViewModel, group: GroupWithCategoriesAndKeywordsData) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceDim)
    ) {
        TableCell(weight = CategoryColumn.CATEGORY.weight) {
            ListItem(
                headlineContent = { Text(group.name) },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceDim,
                    headlineColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        TableCell(weight = CategoryColumn.ACTUAL_SPENDING.weight) {
            val formattedSpending = String.format("%.2f zł", vm.getGroupSpending(group.id))
            InputChip(
                label = { Text(formattedSpending) },
                onClick = {},
                enabled = false,
                selected = false,
                modifier = Modifier.padding(8.dp)
            )
        }
        TableCell(weight = CategoryColumn.MONTHLY_TARGET.weight) {
            val formattedSpending = String.format("%.2f zł", vm.getGroupTarget(group.id))
            InputChip(
                label = { Text(formattedSpending) },
                onClick = {},
                enabled = false,
                selected = false,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun CategoryRow(vm: CategoriesScreenViewModel, category: CategoryWithKeywords) {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        InputDialog(
            title = "Set monthly target",
            onConfirmed = { text ->
                vm.setMonthlyTarget(category.category.id, text.toDouble())
                showDialog = false
            },
            numbersOnly = true,
            onDismiss = { showDialog = false },
            label = "Monthly target",
            initialText = category.category.monthlyTarget.takeIf { it != 0.0 }?.toString(
            ) ?: "0.00"
        )
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
    ) {
        TableCell(weight = CategoryColumn.CATEGORY.weight) {
            ListItem(
                headlineContent = { Text(category.category.name) },
                leadingContent = {
                    Checkbox(
                        checked = category.category.isSelected,
                        onCheckedChange = { vm.toggleCategorySelection(category.category.id) }
                    )
                },
                supportingContent = {
                    val target =
                        category.category.monthlyTarget.takeIf { it != 0.toDouble() } ?: 1.0f
                    CustomLinearProgressIndicator(
                        progress = vm.getCategorySpending(category.category.id)
                            .toFloat() / target.toFloat()
                    )
                }
            )
        }
        TableCell(weight = CategoryColumn.ACTUAL_SPENDING.weight) {
            val formattedSpending =
                String.format("%.2f zł", vm.getCategorySpending(category.category.id))
            InputChip(
                label = { Text(formattedSpending) },
                onClick = { showDialog = true },
                enabled = false,
                selected = false,
                modifier = Modifier.padding(8.dp)
            )
        }
        TableCell(weight = CategoryColumn.MONTHLY_TARGET.weight) {
            val formattedTarget = String.format("%.2f zł", category.category.monthlyTarget)
            InputChip(
                label = { Text(formattedTarget) },
                onClick = { showDialog = true },
                selected = showDialog,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun CustomLinearProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float = 0.0f,
    clipShape: Shape = RoundedCornerShape(16.dp)
) {
    val red = (255 * progress).roundToInt()
    val green = (255 * (1 - progress)).roundToInt()
    val progressColor = Color(red, green, 0)

    Box(
        modifier = modifier
            .clip(clipShape)
            .background(Color.LightGray.copy(alpha = 0.5f))
            .fillMaxWidth()
            .height(8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(progressColor)
                .fillMaxHeight()
                .fillMaxWidth(progress)
        )
    }

}