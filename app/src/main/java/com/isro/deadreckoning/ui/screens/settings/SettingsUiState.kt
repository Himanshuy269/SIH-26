package com.isro.deadreckoning.ui.screens.settings

import androidx.compose.runtime.Immutable

/**
 * UI State for the Settings & Info screen.
 * Annotated with [@Immutable] for optimal Compose recomposition skipping.
 */
@Immutable
data class SettingsUiState(
    val appTitle: String = "AI-ML Intelligent Dead Reckoning System",
    val organization: String = "Indian Space Research Organisation (ISRO)",
    val theme: String = "Smart Vehicles / Intelligent Navigation",
    val datasetName: String = "IO-VNBD Benchmark Dataset",
    val targetFrequencyMobile: String = "10 Hz (Smartphone)",
    val targetFrequencyEdge: String = "200 Hz (Edge / FOG Engine)",
    val maxAllowedDrift: String = "< 10% total distance traveled",
    val selectedAiRuntime: String = "TensorFlow Lite (LiteRT)",
    val isMapMatchingEnabled: Boolean = true,
    val isNonHolonomicConstraintEnabled: Boolean = true,
    val isAiFilterEnabled: Boolean = true
)
