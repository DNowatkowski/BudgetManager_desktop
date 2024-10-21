package org.example.project.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.navigator.Navigator
import org.example.project.ui.components.DateSwitcher
import org.example.project.ui.screens.budgetScreen.BudgetScreen
import org.example.project.ui.screens.keywords.CategoriesScreen
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
            ModalDrawerSheet(modifier = Modifier.width(240.dp)) {
                NavigationDrawerItem(
                    label = { Text(text = "Import") },
                    selected = navigator.lastItem is CategoriesScreen,
                    onClick = {
                        navigator.replace(
                            CategoriesScreen(
                                activeMonth = uiState.activeMonth
                            )
                        )
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Budget") },
                    selected = navigator.lastItem is BudgetScreen,
                    onClick = {
                        navigator.replace(
                            BudgetScreen(
                                activeMonth = uiState.activeMonth,
                            )
                        )
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Reports") },
                    selected = navigator.lastItem is ReportsScreen,
                    onClick = { navigator.replace(ReportsScreen()) }
                )
            }
            Column(modifier = Modifier.fillMaxSize()) {
                DateSwitcher(
                    activeMonth = uiState.activeMonth,
                    onPreviousMonth = { vm.previousMonth() },
                    onNextMonth = { vm.nextMonth() }
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
                            navigator.replace(ReportsScreen())
                        }
                    }
                }
                navigator.lastItem.Content()
            }
        }
    }
}