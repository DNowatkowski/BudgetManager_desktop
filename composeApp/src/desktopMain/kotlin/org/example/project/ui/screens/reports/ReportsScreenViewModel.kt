package org.example.project.ui.screens.reports

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import budgetmanager.composeapp.generated.resources.Res
import budgetmanager.composeapp.generated.resources.expenses
import budgetmanager.composeapp.generated.resources.income
import budgetmanager.composeapp.generated.resources.uncategorized
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.Pie
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.constants.moneyGreen
import org.example.project.domain.models.group.GroupData
import org.example.project.domain.models.group.GroupWithCategoryData
import org.example.project.domain.repositories.CategoryRepository
import org.example.project.domain.repositories.TransactionRepository
import org.jetbrains.compose.resources.getString
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

class ReportsScreenViewModel(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
) : ViewModel() {

    private val _lineChartState = MutableStateFlow(LineChartState())
    val lineChartState = _lineChartState.asStateFlow()

    private val _pieChartState = MutableStateFlow(PieChartState())
    val pieChartState = _pieChartState.asStateFlow()

    private var _queryJob: Job? = null

    init {
        _lineChartState.update {
            it.copy(
                isLoading = true,

                )
        }
        _pieChartState.update {
            it.copy(
                isLoading = true,
            )
        }
    }

    fun getDataForMonth(date: LocalDate) {
        _queryJob?.cancel()
        _queryJob = viewModelScope.launch {
            launch { getLineChartData() }
            launch { getPieChartData(date) }
        }
    }

    private suspend fun getLineChartData() {
        combine(
            categoryRepository.getGroupsWithCategories(),
            transactionRepository.getTransactionsForMonthRange(
                from = LocalDate.now().minusMonths(12),
                to = LocalDate.now()
            )
        ) { groupsWithCategories, transactions ->

            LineChartState(
                isLoading = false,
                isError = null,
                incomeExpenseLines =
                listOf(
                    Line(
                        label = getString(Res.string.income),
                        values = transactions.values.map { list ->
                            val incomeCategories =
                                groupsWithCategories.first { it.group.isIncomeGroup }.categories
                            list.filter { transaction -> incomeCategories.any { it.id == transaction.categoryId } }
                                .sumOf { it.amount }
                                .absoluteValue
                        },
                        color = SolidColor(moneyGreen),
                        dotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(moneyGreen),
                            strokeWidth = 2.dp,
                            radius = 5.dp,
                            strokeColor = SolidColor(moneyGreen),
                        )
                    ),
                    Line(
                        label = getString(Res.string.expenses),
                        values = transactions.values.map { list ->
                            val expenseCategories =
                                groupsWithCategories.first { !it.group.isIncomeGroup }.categories
                            list.filter { transaction -> expenseCategories.any { it.id == transaction.categoryId } || transaction.amount < 0 }
                                .sumOf { it.amount }
                                .absoluteValue
                        },
                        color = SolidColor(Color.Red),
                        dotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(Color.Red),
                            strokeWidth = 2.dp,
                            radius = 5.dp,
                            strokeColor = SolidColor(Color.Red),
                        )
                    )
                ),
                lineChartLabels = transactions.keys.map {
                    val formatter = DateTimeFormatter.ofPattern("MMM yyyy")
                    it.format(formatter)
                }

            )
        }.collectLatest { currentState ->
            _lineChartState.update { currentState }
        }
    }

    private suspend fun getPieChartData(date: LocalDate) {
        combine(
            categoryRepository.getExpenseGroupsWithCategories(),
            transactionRepository.getExpensesForMonth(date)
        ) { groupsWithCategories, expenses ->
            PieChartState(
                isLoading = false,
                groupsWithCategories = groupsWithCategories,
                groupSpending = groupsWithCategories.associate { group ->
                    group.group to expenses
                        .filter { transaction -> group.categories.any { it.id == transaction.categoryId } }
                        .sumOf { it.amount }
                        .absoluteValue
                },
                totalSpending = expenses.sumOf { it.amount }.absoluteValue,
                groupSpendingPercentage = groupsWithCategories.associate { group ->
                    val groupTransactions = expenses
                        .filter { transaction -> group.categories.any { it.id == transaction.categoryId } }
                        .sumOf { it.amount }
                        .absoluteValue
                    val totalTransactions = expenses.sumOf { it.amount }.absoluteValue
                    group.group to ((groupTransactions / totalTransactions) * 100).toInt()
                },
                groupPies = groupsWithCategories.map { group ->
                    Pie(
                        data = group.categories.sumOf { category ->
                            expenses
                                .filter { it.categoryId == category.id }
                                .sumOf { it.amount }
                                .absoluteValue
                        },
                        color = group.group.color,
                        label = group.group.name
                    )
                } + Pie(
                    data = expenses.filter { it.categoryId == null }
                        .sumOf { it.amount }.absoluteValue,
                    color = Color.LightGray,
                    label = getString(Res.string.uncategorized)
                )
            )
        }.collectLatest { currentState ->
            _pieChartState.update { currentState }
        }
    }

    data class LineChartState(
        val isLoading: Boolean = true,
        val isError: Throwable? = null,

        val incomeExpenseLines: List<Line> = emptyList(),
        val lineChartLabels: List<String> = emptyList(),
    )

    data class PieChartState(
        val isLoading: Boolean = true,
        val isError: Throwable? = null,

        val groupPies: List<Pie> = emptyList(),
        val groupsWithCategories: List<GroupWithCategoryData> = emptyList(),
        val groupSpending: Map<GroupData, Double> = emptyMap(),
        val groupSpendingPercentage: Map<GroupData, Int> = emptyMap(),
        val totalSpending: Double = 0.0,
    )
}