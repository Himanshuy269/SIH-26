package com.isro.deadreckoning.domain.engine

import com.isro.deadreckoning.domain.model.GnssData
import com.isro.deadreckoning.domain.model.ImuData
import com.isro.deadreckoning.domain.model.NavigationState
import com.isro.deadreckoning.domain.model.VehicleProfile
import kotlinx.coroutines.flow.StateFlow

/**
 * Master contract for the Navigation Engine.
 */
interface NavigationEngine {

    /**
     * Hot observable stream of calculated vehicle position, speed, and status.
     */
    val navigationState: StateFlow<NavigationState>

    /**
     * Starts the navigation processing loop.
     */
    fun start()

    /**
     * Stops the navigation processing loop.
     */
    fun stop()

    /**
     * Ingests a new 9-DOF IMU reading (Accelerometer, Gyroscope, Magnetometer).
     */
    fun feedImuData(data: ImuData)

    /**
     * Ingests a new GNSS satellite fix when available.
     */
    fun feedGnssData(data: GnssData)

    /**
     * Simulates or triggers a GNSS blackout / outage.
     */
    fun setSimulatedGnssBlackout(enabled: Boolean)

    /**
     * Indicates whether a GNSS blackout is currently active or simulated.
     */
    val isBlackoutSimulated: Boolean

    /**
     * Triggers in-vehicle orientation calibration routine.
     */
    fun recalibrateOrientation()

    /**
     * Sets the active vehicle kinematic profile (Car, Two-Wheeler, Heavy Truck).
     */
    fun setVehicleProfile(profile: VehicleProfile)

    /**
     * Clears recorded 2D trajectory path history.
     */
    fun clearTrajectory()
}
