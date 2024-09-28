package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.entities.CategoryWithKeywords
import org.example.project.data.entities.KeywordEntity
import org.example.project.data.entities.TransactionEntity
import org.example.project.domain.repositories.CategoryRepository
import org.example.project.domain.repositories.KeywordRepository
import org.example.project.domain.repositories.TransactionRepository

class ImportScreenViewModel(
    private val categoryRepository: CategoryRepository,
    val transactionRepository: TransactionRepository,
    private val keywordRepository: KeywordRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getCategoriesWithKeywords()
        }
    }

    private suspend fun getCategoriesWithKeywords() {
        categoryRepository.getCategoriesWithKeywords().collectLatest {
            _uiState.update { currentState ->
                currentState.copy(categoriesWithKeywords = it)
            }
        }
    }

    fun addKeyword(text: String, categoryId: String) {
        viewModelScope.launch {
            keywordRepository.insertKeyword(keywordText = text, categoryId = categoryId)
        }
    }

    fun removeKeyword(keyword:KeywordEntity){
        viewModelScope.launch {
            keywordRepository.deleteKeyword(keyword)
        }
    }

    data class ImportState(
        val isError: Throwable? = null,
        val isLoading: Boolean = false,

        val categoriesWithKeywords: List<CategoryWithKeywords> = emptyList(),
        val transactions: List<TransactionEntity> = emptyList(),
    )
}