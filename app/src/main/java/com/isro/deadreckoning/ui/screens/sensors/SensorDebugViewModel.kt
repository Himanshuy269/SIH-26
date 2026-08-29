package com.isro.deadreckoning.ui.screens.sensors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.isro.deadreckoning.domain.repository.NavigationRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Sensor / Developer Screen.
 *
 * Performance optimized:
 * - Uses [conflate] on high-frequency IMU flows (50Hz) to prevent UI frame stuttering.
 */
class SensorDebugViewModel(
    private val navigationRepository: NavigationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SensorDebugUiState())
    val uiState: StateFlow<SensorDebugUiState> = _uiState.asStateFlow()

    private var mockStreamJob: Job? = null

    /**
     * Toggles whether to display simulated sensor stream or static "--" placeholders.
     */
    fun toggleMockStream(enabled: Boolean) {
        _uiState.update {
            it.copy(
                isMockStreamEnabled = enabled,
                infoMessage = if (enabled) {
                    "Displaying synthetic 9-DOF IMU & GNSS simulation stream."
                } else {
                    "Android hardware sensors are currently disconnected. Values show '--'."
                }
            )
        }

        if (enabled) {
            startCollectingMockStream()
        } else {
            stopCollectingMockStream()
        }
    }

    private fun startCollectingMockStream() {
        mockStreamJob?.cancel()
        mockStreamJob = viewModelScope.launch {
            launch {
                navigationRepository.rawImuStream
                    .conflate()
                    .collect { imu ->
                        if (_uiState.value.isMockStreamEnabled) {
                            _uiState.update { current ->
                                current.copy(
                                    accelX = String.format("%+.2f", imu.accelX),
                                    accelY = String.format("%+.2f", imu.accelY),
                                    accelZ = String.format("%+.2f", imu.accelZ),
                                    accelXRaw = imu.accelX,
                                    accelYRaw = imu.accelY,
                                    accelZRaw = imu.accelZ,
                                    accelStatus = "MOCK FEED (50Hz)",

                                    gyroX = String.format("%+.3f", imu.gyroX),
                                    gyroY = String.format("%+.3f", imu.gyroY),
                                    gyroZ = String.format("%+.3f", imu.gyroZ),
                                    gyroXRaw = imu.gyroX,
                                    gyroYRaw = imu.gyroY,
                                    gyroZRaw = imu.gyroZ,
                                    gyroStatus = "MOCK FEED (50Hz)",

                                    magX = String.format("%+.1f", imu.magX),
                                    magY = String.format("%+.1f", imu.magY),
                                    magZ = String.format("%+.1f", imu.magZ),
                                    magXRaw = imu.magX,
                                    magYRaw = imu.magY,
                                    magZRaw = imu.magZ,
                                    magStatus = "MOCK FEED (50Hz)"
                                )
                            }
                        }
                    }
            }

            launch {
                navigationRepository.rawGnssStream
                    .conflate()
                    .collect { gnss ->
                        if (_uiState.value.isMockStreamEnabled) {
                            _uiState.update { current ->
                                current.copy(
                                    gnssLat = String.format("%.6f°", gnss.latitude),
                                    gnssLon = String.format("%.6f°", gnss.longitude),
                                    gnssSpeed = String.format("%.1f km/h", gnss.speedKmh),
                                    gnssAccuracy = String.format("±%.1f m", gnss.horizontalAccuracyMeters),
                                    gnssStatus = "MOCK FIX (${gnss.satellitesInView} sat)"
                                )
                            }
                        }
                    }
            }
        }
    }

    private fun stopCollectingMockStream() {
        mockStreamJob?.cancel()
        mockStreamJob = null
        _uiState.update {
            it.copy(
                accelX = "--",
                accelY = "--",
                accelZ = "--",
                accelXRaw = 0f,
                accelYRaw = 0f,
                accelZRaw = 0f,
                accelStatus = "NOT CONNECTED",

                gyroX = "--",
                gyroY = "--",
                gyroZ = "--",
                gyroXRaw = 0f,
                gyroYRaw = 0f,
                gyroZRaw = 0f,
                gyroStatus = "NOT CONNECTED",

                magX = "--",
                magY = "--",
                magZ = "--",
                magXRaw = 0f,
                magYRaw = 0f,
                magZRaw = 0f,
                magStatus = "NOT CONNECTED",

                gnssLat = "--",
                gnssLon = "--",
                gnssSpeed = "--",
                gnssAccuracy = "--",
                gnssStatus = "NOT CONNECTED"
            )
        }
    }

    companion object {
        fun provideFactory(
            repository: NavigationRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SensorDebugViewModel(repository) as T
            }
        }
    }
}
