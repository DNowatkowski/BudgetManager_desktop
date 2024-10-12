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
            _uiState.update { currentState ->
                currentState.copy(transactions = it)
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

    data class BudgetState(
        val searchText: String = "",
        val activeMonth: LocalDate = LocalDate.of(2024, 9, 1),
        val groups: List<GroupWithCategoryData> = emptyList(),
        val transactions: List<TransactionData> = emptyList(),
    )

}