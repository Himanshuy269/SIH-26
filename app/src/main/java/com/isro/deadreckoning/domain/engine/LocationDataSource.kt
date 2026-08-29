package com.isro.deadreckoning.domain.engine

import com.isro.deadreckoning.domain.model.GnssData
import kotlinx.coroutines.flow.Flow

/**
 * Abstraction for GNSS / Location data sources.
 *
 * Current implementation: Simulated / Mock Location feed.
 * Future implementation: Android LocationManager / FusedLocationProviderClient.
 */
interface LocationDataSource {

    /**
     * Cold stream emitting raw GNSS satellite fix data.
     */
    val gnssStream: Flow<GnssData>

    /**
     * Starts receiving location updates at the specified interval.
     */
    fun startLocationUpdates(intervalMs: Long = 1000L)

    /**
     * Stops location updates.
     */
    fun stopLocationUpdates()

    /**
     * Returns true if currently tracking location updates.
     */
    val isUpdating: Boolean
}
