package com.isro.deadreckoning.data.repository

import com.isro.deadreckoning.domain.engine.LocationDataSource
import com.isro.deadreckoning.domain.engine.NavigationEngine
import com.isro.deadreckoning.domain.engine.SensorDataSource
import com.isro.deadreckoning.domain.model.GnssData
import com.isro.deadreckoning.domain.model.ImuData
import com.isro.deadreckoning.domain.model.NavigationState
import com.isro.deadreckoning.domain.model.VehicleProfile
import com.isro.deadreckoning.domain.repository.NavigationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Concrete implementation of [NavigationRepository].
 *
 * Orchestrates communication between the [NavigationEngine], [SensorDataSource],
 * and [LocationDataSource]. It forwards raw IMU and GNSS samples to the engine and
 * exposes clean observables to ViewModels.
 */
class NavigationRepositoryImpl(
    private val navigationEngine: NavigationEngine,
    private val sensorDataSource: SensorDataSource,
    private val locationDataSource: LocationDataSource,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : NavigationRepository {

    override val navigationState: StateFlow<NavigationState> = navigationEngine.navigationState
    override val rawImuStream: Flow<ImuData> = sensorDataSource.imuStream
    override val rawGnssStream: Flow<GnssData> = locationDataSource.gnssStream

    override val isBlackoutSimulated: Boolean
        get() = navigationEngine.isBlackoutSimulated

    private var pipingJob: Job? = null

    override fun startSession() {
        navigationEngine.start()
        sensorDataSource.startListening()
        locationDataSource.startLocationUpdates()

        // Pipe sensor streams to the navigation engine
        pipingJob?.cancel()
        pipingJob = coroutineScope.launch {
            launch {
                sensorDataSource.imuStream.collect { imu ->
                    navigationEngine.feedImuData(imu)
                }
            }
            launch {
                locationDataSource.gnssStream.collect { gnss ->
                    navigationEngine.feedGnssData(gnss)
                }
            }
        }
    }

    override fun stopSession() {
        pipingJob?.cancel()
        pipingJob = null
        navigationEngine.stop()
        sensorDataSource.stopListening()
        locationDataSource.stopLocationUpdates()
    }

    override fun toggleGnssBlackout(enableBlackout: Boolean) {
        navigationEngine.setSimulatedGnssBlackout(enableBlackout)
    }

    override fun recalibrate() {
        navigationEngine.recalibrateOrientation()
    }

    override fun setVehicleProfile(profile: VehicleProfile) {
        navigationEngine.setVehicleProfile(profile)
    }

    override fun clearTrajectory() {
        navigationEngine.clearTrajectory()
    }
}
