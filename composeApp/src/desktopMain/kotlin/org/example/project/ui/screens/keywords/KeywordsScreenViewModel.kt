package org.example.project.ui.screens.keywords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import database.KeywordEntity
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.models.CategoryGroupData
import org.example.project.domain.models.KeywordData
import org.example.project.domain.repositories.CategoryRepository
import org.example.project.domain.repositories.KeywordRepository
import org.example.project.domain.repositories.TransactionRepository

class KeywordsScreenViewModel(
    private val categoryRepository: CategoryRepository,
    val transactionRepository: TransactionRepository,
    private val keywordRepository: KeywordRepository,
    private val reader: CsvReader,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getCategoriesWithKeywords()
        }
    }

    private suspend fun getCategoriesWithKeywords() {
        categoryRepository.getAllCategoriesWithData().collectLatest {
            _uiState.update { currentState ->
                currentState.copy(categoryGroups = it)
            }
        }
    }

    fun addCategory(name: String, groupId:String) {
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

    fun importFile(file: PlatformFile?) {
        if (file != null) {
            viewModelScope.launch {
                reader.openAsync(file.file){
                    readAllAsSequence().asFlow().collect{ row->
                        println(row.toString())
                    }
                }
            }
        }
    }

    data class ImportState(
        val isError: Throwable? = null,
        val isLoading: Boolean = false,

        val categoryGroups: List<CategoryGroupData> = emptyList(),
    )
}