package com.isro.deadreckoning.data.mock

import com.isro.deadreckoning.domain.engine.NavigationEngine
import com.isro.deadreckoning.domain.model.GnssData
import com.isro.deadreckoning.domain.model.ImuData
import com.isro.deadreckoning.domain.model.NavigationMode
import com.isro.deadreckoning.domain.model.NavigationState
import com.isro.deadreckoning.domain.model.TrajectoryPoint
import com.isro.deadreckoning.domain.model.VehicleProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.math.cos
import kotlin.math.sin

/**
 * Enhanced Mock implementation of [NavigationEngine] for UI development, testing, and SIH demonstrations.
 *
 * Simulates:
 * - 10 Hz high-fidelity vehicle trajectory with 2D radar breadcrumb points.
 * - Dynamic acceleration and deceleration computation (m/s²).
 * - Vehicle kinematic profiles (Passenger Car, Two-Wheeler, Heavy Truck).
 * - Instantaneous transition between GNSS Fix and Dead Reckoning (INS + AI Fusion).
 * - Controlled positional drift calculation conforming to ISRO benchmark (<10%).
 */
class MockNavigationEngine(
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : NavigationEngine {

    private val _navigationState = MutableStateFlow(
        NavigationState(
            latitude = 12.961050,
            longitude = 77.658040,
            altitudeMeters = 920.0,
            speedKmh = 0f,
            accelerationMps2 = 0f,
            bearingDegrees = 45f,
            accuracyMeters = 1.8f,
            navigationMode = NavigationMode.GNSS_FIX,
            isGnssAvailable = true,
            satellitesCount = 14,
            vehicleProfile = VehicleProfile.CAR,
            debugMessage = "Mock Engine Active (Simulated)"
        )
    )
    override val navigationState: StateFlow<NavigationState> = _navigationState.asStateFlow()

    private var simulationJob: Job? = null
    private var _isBlackoutSimulated = false
    override val isBlackoutSimulated: Boolean
        get() = _isBlackoutSimulated

    // Simulation dynamics state
    private var currentLat = 12.961050
    private var currentLon = 77.658040
    private var currentSpeedKmh = 38.0f
    private var previousSpeedMps = (38.0f * 1000f) / 3600f
    private var currentBearing = 45.0f
    private var blackoutDistanceTraveled = 0f
    private var simulatedDriftMeters = 0f
    private var totalDistanceTraveled = 0f
    private var activeProfile = VehicleProfile.CAR

    // Thread-safe trajectory history buffer (capped at 60 points for high-performance 60fps rendering)
    private val trajectoryPoints = ConcurrentLinkedDeque<TrajectoryPoint>()

    override fun start() {
        if (simulationJob?.isActive == true) return

        simulationJob = coroutineScope.launch {
            var step = 0
            while (isActive) {
                step++
                updateSimulation(step)
                delay(100) // 10 Hz update rate (100ms)
            }
        }
    }

    override fun stop() {
        simulationJob?.cancel()
        simulationJob = null
    }

    override fun feedImuData(data: ImuData) {
        // Will feed future AI model + EKF filter
    }

    override fun feedGnssData(data: GnssData) {
        // Will feed future GNSS fusion module
    }

    override fun setSimulatedGnssBlackout(enabled: Boolean) {
        _isBlackoutSimulated = enabled
        if (enabled) {
            blackoutDistanceTraveled = 0f
            simulatedDriftMeters = 0f
        }
    }

    override fun recalibrateOrientation() {
        _navigationState.update { current ->
            current.copy(
                navigationMode = NavigationMode.CALIBRATING,
                debugMessage = "Calibrating ${activeProfile.displayName} phone mount orientation..."
            )
        }
    }

    override fun setVehicleProfile(profile: VehicleProfile) {
        activeProfile = profile
        _navigationState.update { it.copy(vehicleProfile = profile) }
    }

    override fun clearTrajectory() {
        trajectoryPoints.clear()
        _navigationState.update { it.copy(trajectoryHistory = emptyList()) }
    }

    private fun updateSimulation(step: Int) {
        // 1. Vehicle-specific dynamics parameters
        val (baseTargetSpeed, maxSpeed, agilityMultiplier) = when (activeProfile) {
            VehicleProfile.CAR -> Triple(52.0f, 120.0f, 1.0f)
            VehicleProfile.TWO_WHEELER -> Triple(42.0f, 80.0f, 1.6f)
            VehicleProfile.COMMERCIAL_TRUCK -> Triple(34.0f, 70.0f, 0.6f)
        }

        // 2. Speed oscillation and acceleration calculation
        val speedVariation = sin(step * 0.05).toFloat() * (10f * agilityMultiplier)
        val newSpeedKmh = (baseTargetSpeed + speedVariation).coerceIn(12f, maxSpeed)
        val currentSpeedMps = (newSpeedKmh * 1000f) / 3600f

        // Acceleration in m/s² = (v_new - v_old) / dt (dt = 0.1s)
        val accelerationMps2 = (currentSpeedMps - previousSpeedMps) / 0.1f
        previousSpeedMps = currentSpeedMps
        currentSpeedKmh = newSpeedKmh

        // 3. Heading curvature
        val bearingDelta = (sin(step * 0.03).toFloat() * 1.2f) * agilityMultiplier
        currentBearing = (currentBearing + bearingDelta + 360f) % 360f

        // 4. Coordinates progression
        val distanceDeltaMeters = currentSpeedMps * 0.1f
        totalDistanceTraveled += distanceDeltaMeters

        val bearingRad = Math.toRadians(currentBearing.toDouble())
        val deltaLat = (distanceDeltaMeters * cos(bearingRad)) / 111_000.0
        val deltaLon = (distanceDeltaMeters * sin(bearingRad)) / (111_000.0 * cos(Math.toRadians(currentLat)))

        currentLat += deltaLat
        currentLon += deltaLon

        // 5. Navigation Mode & Drift Logic
        val mode: NavigationMode
        val isGnssOk: Boolean
        val accuracy: Float
        val satellites: Int
        val debugText: String

        if (_isBlackoutSimulated) {
            blackoutDistanceTraveled += distanceDeltaMeters
            // Simulated AI dead reckoning drift (~4.2% of distance, well under 10% benchmark)
            simulatedDriftMeters = blackoutDistanceTraveled * 0.042f

            mode = if (step % 20 > 10) NavigationMode.AI_ENHANCED_FUSION else NavigationMode.DEAD_RECKONING_INS
            isGnssOk = false
            accuracy = 3.5f + (simulatedDriftMeters * 0.4f)
            satellites = 0
            debugText = "GNSS OUTAGE: AI Kinematic Dead Reckoning Active (${activeProfile.displayName})"
        } else {
            mode = NavigationMode.GNSS_FIX
            isGnssOk = true
            accuracy = 1.4f + (sin(step * 0.1f).toFloat() * 0.2f)
            satellites = 14
            simulatedDriftMeters = 0f
            blackoutDistanceTraveled = 0f
            debugText = "NavIC / GNSS Satellite Fix Locked (${activeProfile.displayName})"
        }

        // 6. Record trajectory breadcrumb point (every 3rd step ~ 300ms to preserve smooth memory)
        if (step % 3 == 0) {
            trajectoryPoints.add(
                TrajectoryPoint(
                    latitude = currentLat,
                    longitude = currentLon,
                    mode = mode
                )
            )
            while (trajectoryPoints.size > 60) {
                trajectoryPoints.poll()
            }
        }

        _navigationState.value = NavigationState(
            latitude = currentLat,
            longitude = currentLon,
            altitudeMeters = 920.0 + sin(step * 0.02) * 5.0,
            speedKmh = currentSpeedKmh,
            accelerationMps2 = accelerationMps2,
            bearingDegrees = currentBearing,
            accuracyMeters = accuracy,
            navigationMode = mode,
            isGnssAvailable = isGnssOk,
            timestampMillis = System.currentTimeMillis(),
            driftEstimateMeters = simulatedDriftMeters,
            distanceTraveledMeters = if (_isBlackoutSimulated) blackoutDistanceTraveled else totalDistanceTraveled,
            satellitesCount = satellites,
            vehicleProfile = activeProfile,
            trajectoryHistory = trajectoryPoints.toList(),
            debugMessage = debugText
        )
    }
}
