package org.example.project.ui.screens.budgetScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.models.transaction.TransactionData
import org.example.project.domain.repositories.TransactionRepository
import java.time.LocalDate

class BudgetScreenViewModel(
    private val transactionRepository: TransactionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getTransactions()
        }
    }

    private suspend fun getTransactions() {
        transactionRepository.getTransactionsForMonth(_uiState.value.activeMonth).collectLatest {
            _uiState.update { currentState ->
                currentState.copy(transactions = it)
            }
        }
    }

    data class BudgetState(
        val activeMonth: LocalDate = LocalDate.of(2024, 9, 1),
        val transactions: List<TransactionData> = emptyList(),
    )

}