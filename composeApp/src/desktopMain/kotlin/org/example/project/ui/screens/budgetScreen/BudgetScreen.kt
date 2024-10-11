package org.example.project.ui.screens.budgetScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import org.example.project.domain.models.category.CategoryData
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import java.time.LocalDate

class BudgetScreen : Screen {
    @OptIn(KoinExperimentalAPI::class)
    @Composable
    override fun Content() {
        var showNewTransactionRow by remember { mutableStateOf(false) }

        val vm = koinViewModel<BudgetScreenViewModel>()
        val uiState by vm.uiState.collectAsStateWithLifecycle()

        Column(modifier = Modifier.padding(vertical = 16.dp).fillMaxSize()) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    showNewTransactionRow = true
                }) {
                    Icon(Icons.Filled.AddCircle, null)
                    Text(" Add transaction")
                }
            }
            HorizontalDivider()
            HeaderRow()
            HorizontalDivider()
            LazyColumn {
                items(uiState.transactions.size) { index ->
                    val transaction = uiState.transactions[index]
                    TransactionRow(
                        date = transaction.date,
                        payee = transaction.payee.orEmpty(),
                        description = transaction.description,
                        group = "",
                        categoryId = transaction.categoryId,
                        amount = transaction.amount
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderRow() {
    Row(
        modifier = Modifier.fillMaxWidth().height(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = false,
            onCheckedChange = { },
            modifier = Modifier.weight(0.05f)
        )
        VerticalDivider(modifier = Modifier.padding(horizontal = 8.dp))
        Text(
            "Date",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.weight(0.08f)
        )
        VerticalDivider(modifier = Modifier.padding(horizontal = 8.dp))
        Text(
            "Payee",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.weight(0.2f)
        )
        VerticalDivider(modifier = Modifier.padding(horizontal = 8.dp))
        Text(
            "Description",
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.2f)
        )
        VerticalDivider(modifier = Modifier.padding(horizontal = 8.dp))
        Text(
            "Group",
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.1f)
        )
        VerticalDivider(modifier = Modifier.padding(horizontal = 8.dp))
        Text(
            "Category",
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.1f)
        )
        VerticalDivider(modifier = Modifier.padding(horizontal = 8.dp))
        Text(
            "Amount",
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.08f)
        )
    }
}

@Composable
fun TransactionRow(
    date: LocalDate,
    payee: String,
    description: String,
    group: String,
    categoryId: String?,
    amount: Double,
    groups: List<String> = emptyList(),
    categories: List<CategoryData> = emptyList()
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = false,
            onCheckedChange = { },
            modifier = Modifier.weight(0.05f)
        )
        Text(
            date.toString(),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.08f).padding(horizontal = 8.dp)
        )
        Text(
            payee,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.2f).padding(horizontal = 8.dp)
        )
        Text(
            description,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.2f).padding(horizontal = 8.dp)
        )
        Text(
            group,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.1f).padding(horizontal = 8.dp)
        )
        Text(
            categoryId.toString(),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.1f).padding(horizontal = 8.dp)
        )
        Text(
            text = if (amount < 0) "$amount zł" else "+$amount zł",
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (amount < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            ),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.08f).padding(horizontal = 8.dp)
        )
    }
}