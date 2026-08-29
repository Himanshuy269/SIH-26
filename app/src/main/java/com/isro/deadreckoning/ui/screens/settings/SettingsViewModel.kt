package com.isro.deadreckoning.ui.screens.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel for the Settings & Info Screen.
 */
class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun selectAiRuntime(runtime: String) {
        _uiState.update { it.copy(selectedAiRuntime = runtime) }
    }

    fun toggleMapMatching(enabled: Boolean) {
        _uiState.update { it.copy(isMapMatchingEnabled = enabled) }
    }

    fun toggleNonHolonomicConstraints(enabled: Boolean) {
        _uiState.update { it.copy(isNonHolonomicConstraintEnabled = enabled) }
    }

    fun toggleAiFilter(enabled: Boolean) {
        _uiState.update { it.copy(isAiFilterEnabled = enabled) }
    }
}
