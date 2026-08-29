package com.isro.deadreckoning.domain.engine

import com.isro.deadreckoning.domain.model.ImuData
import kotlinx.coroutines.flow.Flow

/**
 * Abstraction for Inertial Measurement Unit (IMU) sensor sources.
 *
 * Current implementation: [com.isro.deadreckoning.data.mock.MockSensorDataSource]
 * Future implementation: AndroidSensorDataSource (using SensorManager, SensorEventListener for
 * Accelerometer, Gyroscope, and Magnetometer).
 */
interface SensorDataSource {

    /**
     * Cold stream emitting 9-DOF IMU data packets.
     */
    val imuStream: Flow<ImuData>

    /**
     * Starts sampling sensor measurements at the specified rate in microseconds.
     * @param samplingPeriodUs e.g. 10_000 for 100Hz, 20_000 for 50Hz, 5_000 for 200Hz.
     */
    fun startListening(samplingPeriodUs: Int = 20_000)

    /**
     * Stops sensor listeners to preserve device battery when inactive.
     */
    fun stopListening()

    /**
     * Returns true if currently collecting sensor samples.
     */
    val isListening: Boolean
}
