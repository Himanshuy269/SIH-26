package com.isro.deadreckoning.domain.repository

import com.isro.deadreckoning.domain.model.GnssData
import com.isro.deadreckoning.domain.model.ImuData
import com.isro.deadreckoning.domain.model.NavigationState
import com.isro.deadreckoning.domain.model.VehicleProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Domain Repository interface coordinating the Engine, IMU Sensors, and GNSS Data Sources.
 */
interface NavigationRepository {

    /**
     * Observable vehicle navigation state.
     */
    val navigationState: StateFlow<NavigationState>

    /**
     * Observable stream of raw IMU data.
     */
    val rawImuStream: Flow<ImuData>

    /**
     * Observable stream of raw GNSS data.
     */
    val rawGnssStream: Flow<GnssData>

    /**
     * Starts the entire navigation session.
     */
    fun startSession()

    /**
     * Stops the navigation session.
     */
    fun stopSession()

    /**
     * Toggles simulated GNSS blackout mode.
     */
    fun toggleGnssBlackout(enableBlackout: Boolean)

    /**
     * Returns whether GNSS blackout simulation is active.
     */
    val isBlackoutSimulated: Boolean

    /**
     * Initiates orientation recalibration.
     */
    fun recalibrate()

    /**
     * Updates the vehicle kinematics profile.
     */
    fun setVehicleProfile(profile: VehicleProfile)

    /**
     * Clears recorded 2D trajectory history.
     */
    fun clearTrajectory()
}
