package org.example.project.ui.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ehsannarmani.compose_charts.models.Pie
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.models.group.GroupData
import org.example.project.domain.models.group.GroupWithCategoryData
import org.example.project.domain.repositories.CategoryRepository
import org.example.project.domain.repositories.TransactionRepository
import java.time.LocalDate
import kotlin.math.absoluteValue

class ReportsScreenViewModel(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsState())
    val uiState = _uiState.asStateFlow()

    private var _queryJob: Job? = null

    init {
        _uiState.update { it.copy(
            isLoading = true,

        ) }
    }

    fun getDataForMonth(date: LocalDate) {
        _queryJob?.cancel()
        _queryJob = viewModelScope.launch {
            getData(date)
        }
    }

    private suspend fun getData(date: LocalDate) {
        combine(
            categoryRepository.getGroupsWithCategories(),
            transactionRepository.getTransactionsForMonth(date)
        ) { groupsWithCategories, transactions ->
            ReportsState(
                isLoading = false,
                isError = null,
                groupsWithCategories = groupsWithCategories,
                groupSpending = groupsWithCategories.associate { group ->
                    group.group to transactions
                        .filter { transaction -> group.categories.any { it.id == transaction.categoryId } }
                        .sumOf { it.amount }
                        .absoluteValue
                },
                groupPies = groupsWithCategories.map { group ->
                    Pie(
                        data = group.categories.sumOf { category ->
                            transactions
                                .filter { it.categoryId == category.id }
                                .sumOf { it.amount }
                                .absoluteValue
                        },
                        color = group.group.color,
                        label = group.group.name
                    )
                }
            )
        }.collectLatest { currentState ->
            _uiState.update { currentState }

        }
    }


    data class ReportsState(
        val isLoading: Boolean = true,
        val isError: Throwable? = null,
        val groupPies: List<Pie> = emptyList(),
        val groupsWithCategories: List<GroupWithCategoryData> = emptyList(),
        val groupSpending: Map<GroupData, Double> = emptyMap(),
    )
}