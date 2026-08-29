package com.isro.deadreckoning.domain.model

import androidx.compose.runtime.Immutable

/**
 * Immutable representation of the calculated vehicle navigation state.
 *
 * Annotated with [@Immutable] for Jetpack Compose runtime optimization,
 * allowing the Compose compiler to skip recomposition when state instances are equal.
 */
@Immutable
data class NavigationState(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val altitudeMeters: Double = 0.0,
    val speedKmh: Float = 0f,
    val accelerationMps2: Float = 0f,
    val bearingDegrees: Float = 0f,
    val accuracyMeters: Float = 0f,
    val navigationMode: NavigationMode = NavigationMode.INITIALIZING,
    val isGnssAvailable: Boolean = true,
    val timestampMillis: Long = System.currentTimeMillis(),
    val driftEstimateMeters: Float = 0f,
    val distanceTraveledMeters: Float = 0f,
    val satellitesCount: Int = 0,
    val vehicleProfile: VehicleProfile = VehicleProfile.CAR,
    val trajectoryHistory: List<TrajectoryPoint> = emptyList(),
    val debugMessage: String = ""
) {
    /**
     * Helper to compute drift percentage relative to distance traveled during outages.
     * ISRO SIH Benchmark Target: < 10% drift over GNSS-denied intervals.
     */
    val driftPercentage: Float
        get() = if (distanceTraveledMeters > 0f) {
            (driftEstimateMeters / distanceTraveledMeters) * 100f
        } else {
            0f
        }
}
