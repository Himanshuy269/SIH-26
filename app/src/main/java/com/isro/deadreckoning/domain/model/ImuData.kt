package com.isro.deadreckoning.domain.model

import androidx.compose.runtime.Immutable

/**
 * 9-Degrees-of-Freedom (9-DOF) Inertial Measurement Unit (IMU) Data Structure.
 *
 * Annotated with [@Immutable] for optimal Compose rendering performance.
 */
@Immutable
data class ImuData(
    val timestampNanos: Long = 0L,
    // Accelerometer (m/s²)
    val accelX: Float = 0f,
    val accelY: Float = 0f,
    val accelZ: Float = 0f,
    // Gyroscope (rad/s)
    val gyroX: Float = 0f,
    val gyroY: Float = 0f,
    val gyroZ: Float = 0f,
    // Magnetometer (μT)
    val magX: Float = 0f,
    val magY: Float = 0f,
    val magZ: Float = 0f
) {
    /**
     * Total acceleration magnitude = sqrt(x^2 + y^2 + z^2)
     */
    val totalAcceleration: Float
        get() = kotlin.math.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ)

    /**
     * Total angular velocity magnitude = sqrt(x^2 + y^2 + z^2)
     */
    val totalAngularVelocity: Float
        get() = kotlin.math.sqrt(gyroX * gyroX + gyroY * gyroY + gyroZ * gyroZ)
}
