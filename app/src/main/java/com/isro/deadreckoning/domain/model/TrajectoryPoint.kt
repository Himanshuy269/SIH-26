package com.isro.deadreckoning.domain.model

import androidx.compose.runtime.Immutable

/**
 * Represents a single geographical point along the vehicle's navigation trajectory.
 * Used for real-time 2D radar/mini-map breadcrumb plotting on the HUD.
 */
@Immutable
data class TrajectoryPoint(
    val latitude: Double,
    val longitude: Double,
    val mode: NavigationMode,
    val timestampMillis: Long = System.currentTimeMillis()
)
