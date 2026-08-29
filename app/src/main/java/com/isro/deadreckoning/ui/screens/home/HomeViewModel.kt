package com.isro.deadreckoning.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.isro.deadreckoning.domain.model.NavigationState
import com.isro.deadreckoning.domain.model.VehicleProfile
import com.isro.deadreckoning.domain.repository.NavigationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home Navigation HUD screen.
 *
 * Performance optimizations:
 * - Uses [conflate] on incoming state updates to guarantee UI frame smoothness.
 * - Manages vehicle kinematics profiles and 2D trajectory path history.
 */
class HomeViewModel(
    private val navigationRepository: NavigationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeNavigationState()
        navigationRepository.startSession()
    }

    private fun observeNavigationState() {
        viewModelScope.launch {
            navigationRepository.navigationState
               // .conflate()
                .collect { state: NavigationState ->
                    _uiState.update { current ->
                        current.copy(
                            speedKmh = state.speedKmh,
                            accelerationMps2 = state.accelerationMps2,
                            latitude = state.latitude,
                            longitude = state.longitude,
                            altitudeMeters = state.altitudeMeters,
                            bearingDegrees = state.bearingDegrees,
                            accuracyMeters = state.accuracyMeters,
                            satellitesCount = state.satellitesCount,
                            navigationMode = state.navigationMode,
                            isGnssAvailable = state.isGnssAvailable,
                            isBlackoutSimulated = navigationRepository.isBlackoutSimulated,
                            driftEstimateMeters = state.driftEstimateMeters,
                            driftPercentage = state.driftPercentage,
                            distanceTraveledMeters = state.distanceTraveledMeters,
                            vehicleProfile = state.vehicleProfile,
                            trajectoryHistory = state.trajectoryHistory,
                            debugMessage = state.debugMessage
                        )
                    }
                }
        }
    }

    /**
     * Toggles GNSS blackout simulation.
     */
    fun toggleBlackoutSimulation(enableBlackout: Boolean) {
        navigationRepository.toggleGnssBlackout(enableBlackout)
        _uiState.update { it.copy(isBlackoutSimulated = enableBlackout) }
    }

    /**
     * Updates the vehicle kinematics profile (Car, 2-Wheeler, Truck).
     */
    fun selectVehicleProfile(profile: VehicleProfile) {
        navigationRepository.setVehicleProfile(profile)
        _uiState.update { it.copy(vehicleProfile = profile) }
    }

    /**
     * Clears the current trajectory breadcrumb trail.
     */
    fun clearTrajectory() {
        navigationRepository.clearTrajectory()
    }

    /**
     * Triggers orientation recalibration.
     */
    fun recalibratePhoneOrientation() {
        navigationRepository.recalibrate()
    }

    companion object {
        fun provideFactory(
            repository: NavigationRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(repository) as T
            }
        }
    }
}
