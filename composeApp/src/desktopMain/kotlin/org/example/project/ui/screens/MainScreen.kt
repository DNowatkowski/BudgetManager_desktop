package org.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import budgetmanager.composeapp.generated.resources.Res
import budgetmanager.composeapp.generated.resources.categories
import budgetmanager.composeapp.generated.resources.reports
import budgetmanager.composeapp.generated.resources.transactions
import kotlinx.coroutines.launch
import org.example.project.ui.components.DateSwitcher
import org.example.project.ui.screens.budget.BudgetScreen
import org.example.project.ui.screens.categories.CategoriesScreen
import org.example.project.ui.screens.reports.ReportsScreen
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun MainScreen() {
    val vm = koinViewModel<MainScreenViewModel>()
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    Row(modifier = Modifier.fillMaxSize()) {
        ModalDrawerSheet(
            modifier = Modifier.width(250.dp),
            drawerContainerColor = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.9f),
        ) {
            Spacer(Modifier.height(4.dp))
            NavigationItem(
                text = stringResource(Res.string.categories),
                selected = pagerState.currentPage == 0,
                icon = { Icon(Icons.Outlined.Category, contentDescription = "Categories") },
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                }
            )
            NavigationItem(
                text = stringResource(Res.string.transactions),
                selected = pagerState.currentPage == 1,
                icon = {
                    Icon(
                        Icons.Outlined.MonetizationOn,
                        contentDescription = "Transactions"
                    )
                },
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                }
            )
            NavigationItem(
                text = stringResource(Res.string.reports),
                selected = pagerState.currentPage == 2,
                icon = { Icon(Icons.Outlined.BarChart, contentDescription = "Reports") },
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(2)
                    }
                }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surfaceContainer),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DateSwitcher(
                activeMonth = uiState.activeMonth,
                onPreviousMonth = { vm.previousMonth() },
                onNextMonth = { vm.nextMonth() },
                modifier = Modifier.padding(vertical = 8.dp)
            )
            VerticalPager(
                state = pagerState,
                userScrollEnabled = false
            ) { index ->
                when (index) {
                    0 -> {
                        CategoriesScreen(
                            activeMonth = uiState.activeMonth,
                        )
                    }

                    1 -> {
                        BudgetScreen(
                            activeMonth = uiState.activeMonth,
                        )
                    }

                    2 -> {
                        ReportsScreen(
                            activeMonth = uiState.activeMonth,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NavigationItem(
    text: String,
    selected: Boolean,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    NavigationDrawerItem(
        label = { Text(text = text) },
        selected = selected,
        shape = MaterialTheme.shapes.medium,
        icon = icon,
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
            unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
            unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
        ),
        onClick = onClick,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    )
}