package org.example.project.ui.screens.keywords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    init {
        viewModelScope.launch {
            getCategoriesWithKeywords()
        }
        viewModelScope.launch {
            getTransactionsForCurrentMonth()
        }
    }

    private suspend fun getCategoriesWithKeywords() {
        categoryRepository.getGroupsWithCategoriesAndKeywords().collectLatest {
            _uiState.update { currentState ->
                currentState.copy(categoryGroupsWithKeywords = it)
            }
        }
    }

    private suspend fun getTransactionsForCurrentMonth() {
        transactionRepository.getTransactionsForMonth(LocalDate.now()).collectLatest {
            _uiState.update { currentState ->
                currentState.copy(transactions = it)
            }
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

    fun updateGroup(text: String, groupId: String) {
        viewModelScope.launch {
            categoryRepository.updateCategoryGroup(id = groupId, name = text)
        }
    }

    fun removeGroup(groupId: String) {
        viewModelScope.launch {
            categoryRepository.deleteCategoryGroup(groupId)
        }
    }

    fun getCategorySpending(categoryId: String): Double {
        return _uiState.value.transactions.filter { it.categoryId == categoryId }
            .sumOf { it.amount }.absoluteValue
    }

    fun toggleCategorySelection(id: String) {
        _uiState.update { currentState ->
            currentState.copy(
                categoryGroupsWithKeywords = currentState.categoryGroupsWithKeywords.map { group ->
                    group.copy(
                        categories = group.categories.map { category ->
                            if (category.category.id == id) {
                                category.copy(category = category.category.copy(isSelected = !category.category.isSelected))
                            } else {
                                category
                            }
                        }
                    )
                }
            )
        }
    }

    fun moveKeyword(keywordId: String, newCategoryId: String) {
        viewModelScope.launch {
            keywordRepository.moveKeyword(keywordId, newCategoryId)
        }
    }

    data class CategoriesState(
        val isError: Throwable? = null,
        val isLoading: Boolean = false,

        val categoryGroupsWithKeywords: List<GroupWithCategoriesAndKeywordsData> = emptyList(),
        val transactions: List<TransactionData> = emptyList(),
    )
}