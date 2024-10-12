package org.example.project.ui.screens.budgetScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.models.category.CategoryData
import org.example.project.domain.models.group.GroupWithCategoryData
import org.example.project.domain.models.transaction.TransactionData
import org.example.project.domain.repositories.CategoryRepository
import org.example.project.domain.repositories.TransactionRepository
import java.time.LocalDate

class BudgetScreenViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetState())
    val uiState = _uiState.asStateFlow()

    private var _transactions: List<TransactionData> = emptyList()

    init {
        viewModelScope.launch {
            getTransactions()
        }
        viewModelScope.launch {
            getGroupsWithCategories()
        }
    }

    private suspend fun getTransactions() {
        transactionRepository.getTransactionsForMonth(_uiState.value.activeMonth).collectLatest {
            _transactions = it
            _uiState.update { currentState ->
                currentState.copy(
                    transactions = it
                        .filterTransactions(currentState.searchText)
                        .sortTransactions(
                            currentState.sortOption,
                            currentState.sortOrder
                        )
                )
            }
        }
    }

    private suspend fun getGroupsWithCategories() {
        categoryRepository.getGroupsWithCategories().collectLatest {
            _uiState.update { currentState ->
                currentState.copy(groups = it)
            }
        }
    }

    fun toggleTransactionSelection(id: String) {
        _uiState.update { currentState ->
            currentState.copy(
                transactions = currentState.transactions.map {
                    if (it.id == id) {
                        it.copy(isSelected = !it.isSelected)
                    } else {
                        it
                    }
                }
            )
        }
    }

    fun toggleAllTransactionsSelection(value: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                transactions = currentState.transactions.map {
                    it.copy(isSelected = value)
                }
            )
        }
    }

    fun deleteSelectedTransactions() {
        viewModelScope.launch {
            val selectedTransactions = _uiState.value.transactions.filter { it.isSelected }
            transactionRepository.deleteTransactions(selectedTransactions.map { it.id })
        }
    }

    fun resetCategoryForTransaction(transactionId: String, newGroupId: String) {
        viewModelScope.launch {
            transactionRepository.updateCategoryForTransaction(transactionId, null)
        }
    }

    fun updateCategoryForTransaction(transactionId: String, newCategoryId: String) {
        viewModelScope.launch {
            transactionRepository.updateCategoryForTransaction(transactionId, newCategoryId)
        }
    }

    fun updateSearchText(searchText: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchText = searchText,
                transactions = _transactions
                    .filterTransactions(searchText)
                    .sortTransactions(
                        currentState.sortOption,
                        currentState.sortOrder
                    )
            )
        }
    }

    private fun List<TransactionData>.filterTransactions(searchText: String): List<TransactionData> {
        return if (searchText.isEmpty()) {
            this.sortedByDescending { it.date }
        } else {
            this.filter { transaction ->
                transaction.description.contains(
                    searchText,
                    ignoreCase = true
                ) || transaction.payee?.contains(searchText, ignoreCase = true) == true
            }.sortedByDescending { it.date }
        }
    }

    private fun List<TransactionData>.sortTransactions(
        sortOption: TransactionSortOption,
        sortOrder: SortOrder
    ): List<TransactionData> {
        return when (sortOption) {
            TransactionSortOption.DATE -> {
                if (sortOrder == SortOrder.ASCENDING) {
                    this.sortedBy { it.date }
                } else {
                    this.sortedByDescending { it.date }
                }
            }

            TransactionSortOption.AMOUNT -> {
                if (sortOrder == SortOrder.ASCENDING) {
                    this.sortedBy { it.amount }
                } else {
                    this.sortedByDescending { it.amount }
                }
            }
        }
    }

    fun updateSortOption(sortOption: TransactionSortOption) {
        _uiState.update { currentState ->
            currentState.copy(
                sortOption = sortOption,
                transactions = _transactions.filterTransactions(
                    currentState.searchText
                ).sortTransactions(
                    sortOption,
                    currentState.sortOrder
                ),
            )
        }
    }

    fun toggleSortOrder() {
        _uiState.update { currentState ->
            val newSortOrder = if (currentState.sortOrder == SortOrder.ASCENDING) {
                SortOrder.DESCENDING
            } else {
                SortOrder.ASCENDING
            }
            currentState.copy(
                sortOrder = newSortOrder,
                transactions = _transactions.filterTransactions(
                    currentState.searchText
                ).sortTransactions(
                    currentState.sortOption,
                    newSortOrder
                ),
            )
        }
    }

    data class BudgetState(
        val searchText: String = "",
        val activeMonth: LocalDate = LocalDate.of(2024, 9, 1),
        val groups: List<GroupWithCategoryData> = emptyList(),
        val transactions: List<TransactionData> = emptyList(),
        val sortOption: TransactionSortOption = TransactionSortOption.DATE,
        val sortOrder: SortOrder = SortOrder.DESCENDING,
    )

    enum class SortOrder {
        ASCENDING,
        DESCENDING,
    }

    enum class TransactionSortOption {
        DATE,
        AMOUNT,
    }
}