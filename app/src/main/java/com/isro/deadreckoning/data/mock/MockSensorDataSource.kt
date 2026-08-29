package com.isro.deadreckoning.data.mock

import com.isro.deadreckoning.domain.engine.SensorDataSource
import com.isro.deadreckoning.domain.model.ImuData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random
import kotlin.math.cos

/**
 * Mock implementation of [SensorDataSource] generating synthetic 9-DOF IMU streams.
 *
 * Simulates:
 * - Earth gravity (9.81 m/s² on Z-axis)
 * - Longitudinal vehicle acceleration and braking
 * - High-frequency chassis and engine vibration harmonics
 * - Gyroscopic yaw during turning
 * - Geomagnetic field readings for compass heading
 *
 * Later, this class will be replaced by AndroidSensorDataSource without changing UI code.
 */
class MockSensorDataSource(
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : SensorDataSource {

    private val _imuStream = MutableSharedFlow<ImuData>(replay = 1)
    override val imuStream: Flow<ImuData> = _imuStream.asSharedFlow()

    private var samplingJob: Job? = null
    private var _isListening = false
    override val isListening: Boolean
        get() = _isListening

    override fun startListening(samplingPeriodUs: Int) {
        if (samplingJob?.isActive == true) return
        _isListening = true

        val delayMillis = (samplingPeriodUs / 1000L).coerceAtLeast(10L)

        samplingJob = coroutineScope.launch {
            var tick = 0
            while (isActive) {
                tick++
                val nowNanos = System.nanoTime()

                // High-frequency engine & chassis vibration noise
                val vibrationNoise = (Random.nextFloat() - 0.5f) * 0.4f
                // Forward vehicle acceleration wave
                val forwardAccel = (sin(tick * 0.05) * 1.5f).toFloat()

                val sample = ImuData(
                    timestampNanos = nowNanos,
                    // Accelerometer: Forward (Y), Lateral (X), Gravity + Normal (Z)
                    accelX = (sin(tick * 0.08) * 0.3f).toFloat() + vibrationNoise * 0.5f,
                    accelY = forwardAccel + vibrationNoise,
                    accelZ = 9.81f + vibrationNoise * 0.8f,
                    // Gyroscope: Yaw angular velocity during vehicle turns
                    gyroX = (sin(tick * 0.1) * 0.02f).toFloat(),
                    gyroY = (
                            cos(tick * 0.1) * 0.02f).toFloat(),
                    gyroZ = (sin(tick * 0.03) * 0.08f).toFloat(),
                    // Magnetometer: Ambient geomagnetic field (μT)
                    magX = 22.4f + (sin(tick * 0.02) * 3f).toFloat(),
                    magY = -12.8f + (cos(tick * 0.02) * 3f).toFloat(),
                    magZ = 41.2f
                )

                _imuStream.emit(sample)
                delay(delayMillis)
            }
        }
    }

    override fun stopListening() {
        samplingJob?.cancel()
        samplingJob = null
        _isListening = false
    }
}
