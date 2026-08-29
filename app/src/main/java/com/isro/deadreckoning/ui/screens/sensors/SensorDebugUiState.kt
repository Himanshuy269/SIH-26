package com.isro.deadreckoning.ui.screens.sensors

import androidx.compose.runtime.Immutable

/**
 * UI State for Developer Sensor Debug Screen.
 * Annotated with [@Immutable] for optimal Compose rendering performance.
 */
@Immutable
data class SensorDebugUiState(
    // Accelerometer (m/s²)
    val accelX: String = "--",
    val accelY: String = "--",
    val accelZ: String = "--",
    val accelXRaw: Float = 0f,
    val accelYRaw: Float = 0f,
    val accelZRaw: Float = 0f,
    val accelStatus: String = "NOT CONNECTED",

    // Gyroscope (rad/s)
    val gyroX: String = "--",
    val gyroY: String = "--",
    val gyroZ: String = "--",
    val gyroXRaw: Float = 0f,
    val gyroYRaw: Float = 0f,
    val gyroZRaw: Float = 0f,
    val gyroStatus: String = "NOT CONNECTED",

    // Magnetometer (μT)
    val magX: String = "--",
    val magY: String = "--",
    val magZ: String = "--",
    val magXRaw: Float = 0f,
    val magYRaw: Float = 0f,
    val magZRaw: Float = 0f,
    val magStatus: String = "NOT CONNECTED",

    // GNSS Raw Fix
    val gnssLat: String = "--",
    val gnssLon: String = "--",
    val gnssSpeed: String = "--",
    val gnssAccuracy: String = "--",
    val gnssStatus: String = "NOT CONNECTED",

    // Mode toggle
    val isMockStreamEnabled: Boolean = false,
    val infoMessage: String = "Android hardware sensors are currently disconnected. Values show '--'."
)
