package com.isro.deadreckoning.core.constants

/**
 * Global application constants and configuration parameters for the SIH project.
 */
object AppConstants {
    const val APP_NAME = "ISRO Intelligent Dead Reckoning"
    const val SIH_THEME = "Smart Vehicles - Intelligent Dead Reckoning"
    const val ISRO_DEPT = "Indian Space Research Organisation (ISRO)"

    // Performance targets as specified in SIH Problem Statement
    const val TARGET_MOBILE_UPDATE_RATE_HZ = 10     // 10 Hz update rate for smartphone
    const val TARGET_EDGE_UPDATE_RATE_HZ = 200      // 200 Hz update rate for Edge/FOG platform
    const val MAX_ALLOWED_DRIFT_PERCENT = 10f       // Drift benchmark: < 10% of total distance

    // Sensor sampling intervals
    const val SENSOR_SAMPLING_PERIOD_US_NORMAL = 20_000 // 50 Hz (20ms)
    const val SENSOR_SAMPLING_PERIOD_US_FAST = 10_000   // 100 Hz (10ms)
}
