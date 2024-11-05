package org.example.project.ui.screens.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.constants.ImportOptions
import org.example.project.data.dto.MillenniumTransactionDto
import org.example.project.data.dto.SantanderTransactionDto
import org.example.project.data.dto.TransactionDto
import org.example.project.domain.models.group.GroupWithCategoryData
import org.example.project.domain.models.keyword.IgnoredKeywordData
import org.example.project.domain.models.stringToDouble
import org.example.project.domain.models.toDomainModel
import org.example.project.domain.models.toLocalDate
import org.example.project.domain.models.transaction.TransactionData
import org.example.project.domain.repositories.CategoryRepository
import org.example.project.domain.repositories.KeywordRepository
import org.example.project.domain.repositories.TransactionRepository
import org.example.project.utils.BankTransactionParserFactory
import java.io.InputStream
import java.time.LocalDate

class BudgetScreenViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val parserFactory: BankTransactionParserFactory,
    private val keywordRepository: KeywordRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetState())
    val uiState = _uiState.asStateFlow()

    private var _transactions: List<TransactionData> = emptyList()
    private var _queryJob: Job? = null

    init {
        _queryJob = viewModelScope.launch {
            getTransactions(LocalDate.now())
        }
        viewModelScope.launch {
            getGroupsWithCategories()
        }
        viewModelScope.launch {
            getIgnoredKeywords()
        }
    }

    private suspend fun getTransactions(date: LocalDate) {
        transactionRepository.getTransactionsForMonth(date).collectLatest {
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

    fun getTransactionsForMonth(date: LocalDate) {
        _queryJob?.cancel()
        _queryJob = viewModelScope.launch {
            getTransactions(date)
        }
    }

    private suspend fun getGroupsWithCategories() {
        categoryRepository.getGroupsWithCategories().collectLatest {
            _uiState.update { currentState ->
                currentState.copy(groups = it)
            }
        }
    }

    private suspend fun getIgnoredKeywords() {
        keywordRepository.getIgnoredKeywords().collectLatest {
            _uiState.update { currentState ->
                currentState.copy(ignoredKeywords = it)
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

    fun addTransaction(transaction: TransactionData) {
        viewModelScope.launch {
            transactionRepository.insertTransaction(transaction)
        }
    }

    fun importFile(stream: InputStream?, importOptions: ImportOptions) {
        val parser = parserFactory.getParser(importOptions.bankType) ?: return

        viewModelScope.launch {
            val list = parser.parseTransactions(stream)
                .filterIgnored()
                .applyFilters(importOptions)
                .applyValueDivision(importOptions)
                .removeDuplicates(importOptions)
                .map { it.toDomainModel() }

            transactionRepository.insertTransactions(list)
        }
    }

    private fun List<TransactionDto>.filterIgnored(): List<TransactionDto> {
        return this.filter { transaction ->
            val ignoredKeywords = _uiState.value.ignoredKeywords.map { it.keyword }
            !ignoredKeywords.any {
                transaction.description.contains(
                    it,
                    ignoreCase = true
                ) || transaction.payee.contains(it, ignoreCase = true)
            }
        }
    }

    private fun List<TransactionDto>.applyFilters(importOptions: ImportOptions): List<TransactionDto> {
        return if (importOptions.dateTo != null && importOptions.dateFrom != null) {
            this.filter {
                val localDate = it.date.toLocalDate()
                localDate.isAfter(importOptions.dateFrom) && localDate.isBefore(importOptions.dateTo)
            }

        } else {
            this
        }
    }

    private fun List<TransactionDto>.applyValueDivision(importOptions: ImportOptions): List<TransactionDto> {
        return if (importOptions.valuesDividedBy != 1) {
            this.map {
                when (it) {
                    is MillenniumTransactionDto -> {
                        val dividedExpense =
                            it.expense.stringToDouble() / importOptions.valuesDividedBy
                        val dividedIncome =
                            it.income.stringToDouble() / importOptions.valuesDividedBy
                        it.copy(
                            expense = String.format("%.2f", dividedExpense),
                            income = String.format("%.2f", dividedIncome)
                        )
                    }

                    is SantanderTransactionDto -> {
                        val dividedAmount =
                            it.amount.stringToDouble() / importOptions.valuesDividedBy
                        it.copy(amount = String.format("%.2f", dividedAmount))
                    }

                    else -> {
                        it
                    }
                }
            }
        } else {
            this
        }
    }

    private suspend fun List<TransactionDto>.removeDuplicates(importOptions: ImportOptions): List<TransactionDto> {
        if (!importOptions.skipDuplicates) {
            return this
        } else {
            val list = this.toMutableList()
            coroutineScope {
                val jobs = this@removeDuplicates.map { transaction ->
                    async {
                        val duplicate =
                            transactionRepository.getTransactionsForDay(transaction.date.toLocalDate())
                                .firstOrNull()?.find {

                                    when (transaction) {
                                        is MillenniumTransactionDto -> {
                                            transaction.description == it.description &&
                                                    (transaction.income.stringToDouble() == it.amount || transaction.expense.stringToDouble() == it.amount)
                                        }

                                        is SantanderTransactionDto -> {
                                            transaction.description == it.description && transaction.amount.stringToDouble() == it.amount
                                        }

                                        else -> {
                                            false
                                        }
                                    }

                                }
                        if (duplicate != null) {
                            list.remove(transaction)
                        }
                    }
                }
                jobs.awaitAll()
            }
            return list
        }
    }

    fun deleteSelectedTransactions() {
        viewModelScope.launch {
            val selectedTransactions = _uiState.value.transactions.filter { it.isSelected }
            transactionRepository.deleteTransactions(selectedTransactions.map { it.id })
        }
    }

    fun resetCategoryForTransaction(transactionId: String) {
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

    fun addIgnoredKeyword(keyword: String) {
        viewModelScope.launch {
            keywordRepository.insertIgnoredKeyword(keyword)
        }
    }

    fun removeIgnoredKeyword(keywordId: String) {
        viewModelScope.launch {
            keywordRepository.deleteIgnoredKeyword(keywordId)
        }
    }

    fun updateIgnoredKeyword(keyword: IgnoredKeywordData) {
        viewModelScope.launch {
            keywordRepository.updateIgnoredKeyword(keyword)
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
                        || transaction.amount.toString().contains(searchText, ignoreCase = true)
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
        val groups: List<GroupWithCategoryData> = emptyList(),
        val transactions: List<TransactionData> = emptyList(),
        val sortOption: TransactionSortOption = TransactionSortOption.DATE,
        val sortOrder: SortOrder = SortOrder.DESCENDING,
        val ignoredKeywords: List<IgnoredKeywordData> = emptyList(),
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