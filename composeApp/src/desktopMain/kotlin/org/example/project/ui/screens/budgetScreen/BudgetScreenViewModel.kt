package org.example.project.ui.screens.budgetScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
                currentState.copy(transactions = it.filterTransactions(currentState.searchText))
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
                transactions = _transactions.filterTransactions(searchText)
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

    data class BudgetState(
        val searchText: String = "",
        val activeMonth: LocalDate = LocalDate.of(2024, 9, 1),
        val groups: List<GroupWithCategoryData> = emptyList(),
        val transactions: List<TransactionData> = emptyList(),
    )

}