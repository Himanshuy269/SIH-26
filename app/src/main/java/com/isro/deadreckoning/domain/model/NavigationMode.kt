package com.isro.deadreckoning.domain.model

/**
 * Represents the current operational mode of the navigation system.
 *
 * Direct mapping to the SIH ISRO Problem Statement:
 * - When GNSS signals are healthy, the system operates in [GNSS_FIX] or [GNSS_AIDED_INS].
 * - When entering tunnels, underpasses, or urban canyons, it instantly transitions to
 *   [DEAD_RECKONING_INS] or [AI_ENHANCED_FUSION].
 */
enum class NavigationMode(val displayName: String, val description: String) {
    /**
     * Standard high-accuracy GNSS navigation with clear satellite visibility.
     */
    GNSS_FIX(
        displayName = "GNSS Fix (Satellite)",
        description = "High accuracy satellite navigation active"
    ),

    /**
     * GNSS + Inertial Navigation System fusion for optimal lane-level accuracy.
     */
    GNSS_AIDED_INS(
        displayName = "GNSS + INS Fusion",
        description = "Optimal fusion of GNSS and IMU sensors"
    ),

    /**
     * GNSS Outage / Blackout: Pure inertial dead reckoning using accelerometer and gyroscope integration.
     */
    DEAD_RECKONING_INS(
        displayName = "Dead Reckoning (INS)",
        description = "GNSS outage active: tracking vehicle position purely via IMU"
    ),

    /**
     * GNSS Outage / Blackout with AI/ML enhancements:
     * - AI Speed & Vibration Filter (removes engine vibrations & potholes)
     * - AI velocity prediction from IMU kinematics
     * - Map-Matching with Non-Holonomic Constraints (NHC)
     */
    AI_ENHANCED_FUSION(
        displayName = "AI-Enhanced Dead Reckoning",
        description = "AI kinematic velocity estimation & road map-matching active"
    ),

    /**
     * In-Vehicle Alignment & Calibration Engine determining phone pitch/roll/yaw.
     */
    CALIBRATING(
        displayName = "In-Vehicle Calibration",
        description = "Calibrating phone orientation relative to vehicle chassis"
    ),

    /**
     * Unknown or initializing state before first sensor/GNSS fix.
     */
    INITIALIZING(
        displayName = "Initializing",
        description = "Acquiring initial sensors and location fix"
    )
}
