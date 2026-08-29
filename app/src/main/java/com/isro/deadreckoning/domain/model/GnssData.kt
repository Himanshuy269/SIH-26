package com.isro.deadreckoning.domain.model

import androidx.compose.runtime.Immutable

/**
 * Raw GNSS (GPS/NavIC/Galileo) positioning data model.
 *
 * Annotated with [@Immutable] for optimal Compose rendering performance.
 */
@Immutable
data class GnssData(
    val timestampMillis: Long = 0L,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val altitudeMeters: Double = 0.0,
    val speedMps: Float = 0f,
    val bearingDegrees: Float = 0f,
    val horizontalAccuracyMeters: Float = 0f,
    val satellitesInView: Int = 0,
    val hasFix: Boolean = false
) {
    val speedKmh: Float
        get() = speedMps * 3.6f
}
