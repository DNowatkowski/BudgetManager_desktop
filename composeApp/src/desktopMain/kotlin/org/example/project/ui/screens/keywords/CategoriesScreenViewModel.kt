package org.example.project.ui.screens.keywords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.models.category.CategoryData
import org.example.project.domain.models.group.GroupData
import org.example.project.domain.models.group.GroupWithCategoriesAndKeywordsData
import org.example.project.domain.models.keyword.KeywordData
import org.example.project.domain.models.transaction.TransactionData
import org.example.project.domain.repositories.CategoryRepository
import org.example.project.domain.repositories.KeywordRepository
import org.example.project.domain.repositories.TransactionRepository
import java.time.LocalDate
import kotlin.math.absoluteValue

class CategoriesScreenViewModel(
    private val categoryRepository: CategoryRepository,
    private val keywordRepository: KeywordRepository,
    private val transactionRepository: TransactionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesState())
    val uiState = _uiState.asStateFlow()

    private var _queryJob: Job? = null

    private suspend fun getData(date: LocalDate) {
        combine(
            categoryRepository.getGroupsWithCategoriesAndKeywords(),
            transactionRepository.getTransactionsForMonth(date)
        ) { groupWithCategoriesAndKeywords, transactions ->
            CategoriesState(
                isLoading = false,
                isError = null,
                categoryGroupsWithKeywords = groupWithCategoriesAndKeywords,
                transactions = transactions,
                groupTargets = groupWithCategoriesAndKeywords.associate { group ->
                    group.group to group.categories.sumOf { it.category.monthlyTarget }.absoluteValue
                },

                groupSpending = groupWithCategoriesAndKeywords.associate { group ->
                    group.group to transactions
                        .filter { transaction -> group.categories.any { it.category.id == transaction.categoryId } }
                        .sumOf { it.amount }
                        .absoluteValue
                },
                categorySpending = groupWithCategoriesAndKeywords.flatMap { it.categories }
                    .associate { category ->
                        category.category to transactions
                            .filter { it.categoryId == category.category.id }
                            .sumOf { it.amount }
                            .absoluteValue
                    }

            )
        }.collectLatest { currentState ->
            _uiState.update { currentState }

        }
    }

    fun getDataForMonth(date: LocalDate) {
        _queryJob?.cancel()
        _queryJob = viewModelScope.launch {
            getData(date)
        }
    }

    fun addCategory(name: String, groupId: String) {
        viewModelScope.launch {
            categoryRepository.insertCategory(name = name, groupId = groupId)
        }
    }

    fun addKeyword(text: String, categoryId: String) {
        viewModelScope.launch {
            keywordRepository.insertKeyword(keywordText = text, categoryId = categoryId)
        }
    }

    fun removeKeyword(keyword: KeywordData) {
        viewModelScope.launch {
            keywordRepository.deleteKeyword(keyword.id)
        }
    }

    fun updateKeyword(keyword: KeywordData) {
        viewModelScope.launch {
            keywordRepository.updateKeyword(keyword)
        }
    }

    fun addGroup(text: String) {
        viewModelScope.launch {
            categoryRepository.insertCategoryGroup(text)
        }
    }

    fun updateGroup(name: String, groupId: String) {
        viewModelScope.launch {
            categoryRepository.updateCategoryGroup(id = groupId, name = name)
        }
    }

    fun updateCategory(categoryId: String, name: String) {
        viewModelScope.launch {
            categoryRepository.updateCategory(categoryId, name)
        }
    }

    fun removeGroup(groupId: String) {
        viewModelScope.launch {
            categoryRepository.deleteCategoryGroup(groupId)
        }
    }

    fun removeCategory(categoryId: String) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(categoryId)
        }
    }

    fun setMonthlyTarget(categoryId: String, target: Double) {
        viewModelScope.launch {
            categoryRepository.updateMonthlyTarget(categoryId = categoryId, target = target)
        }
    }

//    fun moveKeyword(keywordId: String, newCategoryId: String) {
//        viewModelScope.launch {
//            keywordRepository.moveKeyword(keywordId, newCategoryId)
//        }
//    }


    data class CategoriesState(
        val isError: Throwable? = null,
        val isLoading: Boolean = false,

        val categoryGroupsWithKeywords: List<GroupWithCategoriesAndKeywordsData> = emptyList(),
        val transactions: List<TransactionData> = emptyList(),
        val groupTargets: Map<GroupData, Double> = emptyMap(),
        val groupSpending: Map<GroupData, Double> = emptyMap(),
        val categorySpending: Map<CategoryData, Double> = emptyMap()
    )
}