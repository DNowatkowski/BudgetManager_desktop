package org.example.project.ui.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import budgetmanager.composeapp.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.project.domain.models.IconData
import org.example.project.domain.models.category.CategoryData
import org.example.project.domain.models.group.GroupData
import org.example.project.domain.models.group.GroupWithCategoriesAndKeywordsData
import org.example.project.domain.models.keyword.KeywordData
import org.example.project.domain.models.transaction.TransactionData
import org.example.project.domain.repositories.CategoryRepository
import org.example.project.domain.repositories.KeywordRepository
import org.example.project.domain.repositories.TransactionRepository
import org.example.project.utils.ImageUtil
import org.jetbrains.compose.resources.ExperimentalResourceApi
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDate
import kotlin.math.absoluteValue

class CategoriesScreenViewModel(
    private val categoryRepository: CategoryRepository,
    private val keywordRepository: KeywordRepository,
    private val transactionRepository: TransactionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesState())
    val uiState = _uiState.asStateFlow()

    private val _iconsState = MutableStateFlow(IconsState())
    val iconsState = _iconsState.asStateFlow()

    private var _searchJob: Job? = null
    private var _icons = listOf<IconData>()

    init {
        viewModelScope.launch {
            getIcons()
        }
    }

    private suspend fun getData(date: LocalDate) {
        combine(
            categoryRepository.getGroupsWithCategoriesAndKeywords(),
            transactionRepository.getTransactionsForMonth(date)
        ) { groupWithCategoriesAndKeywords, transactions ->
            CategoriesState(
                isLoading = false,
                isError = null,
                categoryGroupsWithKeywords = groupWithCategoriesAndKeywords.sortedByDescending { it.group.isIncomeGroup },
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

    fun updateSearchText(text: String) {
        _iconsState.update { currentState ->
            currentState.copy(
                searchText = text,
                icons = _icons.filter { it.name.contains(text, ignoreCase = true) }
            )
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    private suspend fun getIcons() {
        _iconsState.update {
            it.copy(
                isLoading = true
            )
        }
        val uri = Res.readBytes("files/icon-names.txt").inputStream()
        val reader = BufferedReader(InputStreamReader(uri))
        val lines = reader.readLines()
        withContext(Dispatchers.IO) {
            reader.close()
        }
        _icons = lines
            .map { parseIconItem(it) }
            .filter { it.image != null }

        _iconsState.update {
            it.copy(
                isLoading = false,
                icons = _icons
            )
        }
    }

    fun setIconForGroup(groupId: String, iconId: String) {
        viewModelScope.launch {
            categoryRepository.updateGroupIcon(groupId = groupId, iconId = iconId)
            updateSearchText("")
        }
    }

    private fun parseIconItem(line: String): IconData {
        val split = line.split(",")
        val id = split[0]
        val name = split[1]
        val image = ImageUtil.createImageVector(id)

        return IconData(id, name, image)
    }

    fun getDataForMonth(date: LocalDate) {
        _searchJob?.cancel()
        _searchJob = viewModelScope.launch {
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

    data class IconsState(
        val isLoading: Boolean = false,

        val searchText: String = "",
        val icons: List<IconData> = emptyList()
    )
}