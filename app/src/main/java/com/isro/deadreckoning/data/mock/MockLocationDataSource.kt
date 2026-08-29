package com.isro.deadreckoning.data.mock

import com.isro.deadreckoning.domain.engine.LocationDataSource
import com.isro.deadreckoning.domain.model.GnssData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Mock implementation of [LocationDataSource] simulating periodic GNSS satellite updates.
 */
class MockLocationDataSource(
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : LocationDataSource {

    private val _gnssStream = MutableSharedFlow<GnssData>(replay = 1)
    override val gnssStream: Flow<GnssData> = _gnssStream.asSharedFlow()

    private var updateJob: Job? = null
    private var _isUpdating = false
    override val isUpdating: Boolean
        get() = _isUpdating

    override fun startLocationUpdates(intervalMs: Long) {
        if (updateJob?.isActive == true) return
        _isUpdating = true

        updateJob = coroutineScope.launch {
            while (isActive) {
                val data = GnssData(
                    timestampMillis = System.currentTimeMillis(),
                    latitude = 12.961050,
                    longitude = 77.658040,
                    altitudeMeters = 920.0,
                    speedMps = 14.5f, // ~52 km/h
                    bearingDegrees = 45f,
                    horizontalAccuracyMeters = 1.6f,
                    satellitesInView = 14,
                    hasFix = true
                )
                _gnssStream.emit(data)
                delay(intervalMs)
            }
        }
    }

    override fun stopLocationUpdates() {
        updateJob?.cancel()
        updateJob = null
        _isUpdating = false
    }
}
