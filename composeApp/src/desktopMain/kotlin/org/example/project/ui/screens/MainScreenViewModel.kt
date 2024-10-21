package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

class MainScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainScreenState())
    val uiState = _uiState.asStateFlow()

    fun previousMonth() {
        _uiState.update {
            it.copy(activeMonth = it.activeMonth.minusMonths(1))
        }

    }

    fun nextMonth() {
        _uiState.update {
            it.copy(activeMonth = it.activeMonth.plusMonths(1))
        }
    }

    data class MainScreenState(
        val activeMonth: LocalDate = LocalDate.now(),
    )
}