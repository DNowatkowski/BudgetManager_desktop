package org.example.project.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideOrientation
import cafe.adriel.voyager.transitions.SlideTransition
import org.example.project.ui.screens.budgetScreen.BudgetScreen
import org.example.project.ui.screens.keywords.KeywordsScreen


@Composable
fun MainScreen() {
    Navigator(KeywordsScreen()) { navigator ->
        Row(modifier = Modifier.fillMaxSize()) {
            ModalDrawerSheet(modifier = Modifier.width(240.dp)) {
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
            Column(modifier = Modifier.fillMaxSize()) {
                SlideTransition(navigator = navigator, orientation = SlideOrientation.Vertical)
            }
        }
    }
}