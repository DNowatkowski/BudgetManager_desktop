package org.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.navigator.Navigator
import org.example.project.ui.components.DateSwitcher
import org.example.project.ui.screens.budget.BudgetScreen
import org.example.project.ui.screens.categories.CategoriesScreen
import org.example.project.ui.screens.reports.ReportsScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import java.time.LocalDate


@OptIn(KoinExperimentalAPI::class)
@Composable
fun MainScreen() {
    val vm = koinViewModel<MainScreenViewModel>()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    Navigator(
        CategoriesScreen(
            activeMonth = LocalDate.now(),
        )
    ) { navigator ->
        Row(modifier = Modifier.fillMaxSize()) {
            ModalDrawerSheet(
                modifier = Modifier.width(250.dp),
                drawerContainerColor = MaterialTheme.colorScheme.primaryContainer,
            ) {
                NavigationItem(
                    text = "Categories",
                    selected = navigator.lastItem is CategoriesScreen,
                    icon = { Icon(Icons.Outlined.Category, contentDescription = "Categories") },
                    onClick = {
                        navigator.replace(
                            CategoriesScreen(
                                activeMonth = uiState.activeMonth
                            )
                        )
                    }
                )
                NavigationItem(
                    text = "Transactions",
                    selected = navigator.lastItem is BudgetScreen,
                    icon = {
                        Icon(
                            Icons.Outlined.MonetizationOn,
                            contentDescription = "Transactions"
                        )
                    },
                    onClick = {
                        navigator.replace(
                            BudgetScreen(
                                activeMonth = uiState.activeMonth,
                            )
                        )
                    }
                )
                NavigationItem(
                    text = "Reports",
                    selected = navigator.lastItem is ReportsScreen,
                    icon = { Icon(Icons.Outlined.BarChart, contentDescription = "Reports") },
                    onClick = {
                        navigator.replace(ReportsScreen(uiState.activeMonth))
                    }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                DateSwitcher(
                    activeMonth = uiState.activeMonth,
                    onPreviousMonth = { vm.previousMonth() },
                    onNextMonth = { vm.nextMonth() },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                LaunchedEffect(uiState.activeMonth) {
                    when (navigator.lastItem) {
                        is CategoriesScreen -> {
                            navigator.replace(
                                CategoriesScreen(
                                    activeMonth = uiState.activeMonth
                                )
                            )
                        }

                        is BudgetScreen -> {
                            navigator.replace(
                                BudgetScreen(
                                    activeMonth = uiState.activeMonth,
                                )
                            )
                        }

                        is ReportsScreen -> {
                            navigator.replace(ReportsScreen(uiState.activeMonth))
                        }
                    }
                }
                navigator.lastItem.Content()
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
            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
            selectedTextColor = MaterialTheme.colorScheme.primary,
            selectedIconColor = MaterialTheme.colorScheme.primary
        ),
        onClick = onClick,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    )
}