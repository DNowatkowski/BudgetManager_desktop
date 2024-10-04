package org.example.project.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import org.example.project.ui.screens.keywords.KeywordsScreen


@Composable fun MainScreen(){
    Navigator(BudgetScreen()) { navigator ->
        Row {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    label = { Text(text = "Import") },
                    selected = navigator.lastItem is KeywordsScreen,
                    onClick = { navigator.replace(KeywordsScreen()) }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Budget") },
                    selected = navigator.lastItem is BudgetScreen,
                    onClick = { navigator.replace(BudgetScreen()) }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Reports") },
                    selected = navigator.lastItem is ReportsScreen,
                    onClick = { navigator.replace(ReportsScreen()) }
                )

            }
            CurrentScreen()
        }
    }
}