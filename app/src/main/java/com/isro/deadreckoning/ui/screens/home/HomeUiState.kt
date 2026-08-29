package com.isro.deadreckoning.ui.screens.home

import androidx.compose.runtime.Immutable
import com.isro.deadreckoning.domain.model.NavigationMode
import com.isro.deadreckoning.domain.model.TrajectoryPoint
import com.isro.deadreckoning.domain.model.VehicleProfile

/**
 * UI State for the Home / Navigation HUD screen.
 * Annotated with [@Immutable] for optimal Jetpack Compose recomposition skipping.
 */
@Immutable
data class HomeUiState(
    val speedKmh: Float = 0f,
    val accelerationMps2: Float = 0f,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val altitudeMeters: Double = 0.0,
    val bearingDegrees: Float = 0f,
    val accuracyMeters: Float = 0f,
    val satellitesCount: Int = 0,
    val navigationMode: NavigationMode = NavigationMode.INITIALIZING,
    val isGnssAvailable: Boolean = true,
    val isBlackoutSimulated: Boolean = false,
    val driftEstimateMeters: Float = 0f,
    val driftPercentage: Float = 0f,
    val distanceTraveledMeters: Float = 0f,
    val vehicleProfile: VehicleProfile = VehicleProfile.CAR,
    val trajectoryHistory: List<TrajectoryPoint> = emptyList(),
    val debugMessage: String = "Initializing..."
)
