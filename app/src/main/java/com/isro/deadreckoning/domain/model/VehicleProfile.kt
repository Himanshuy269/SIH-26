package com.isro.deadreckoning.domain.model

import androidx.compose.runtime.Immutable

/**
 * Defines the vehicle kinematics profile.
 *
 * Specifically addresses the SIH ISRO Problem Statement:
 * "While modern high-end cars possess factory-fitted INS, the vast majority of vehicles on Indian
 * roads—including commercial trucks, older cars, and millions of two-wheelers (motorcycles/scooters)—
 * rely solely on the driver's smartphone mounted on the dashboard or placed in mobile holder."
 */
@Immutable
enum class VehicleProfile(
    val displayName: String,
    val description: String,
    val maxSpeedKmh: Float,
    val typicalVibrationLevel: String
) {
    CAR(
        displayName = "Passenger Car",
        description = "Standard 4-wheeler chassis dynamics & moderate engine harmonics",
        maxSpeedKmh = 120f,
        typicalVibrationLevel = "Moderate (0.3 m/s²)"
    ),

    TWO_WHEELER(
        displayName = "Two-Wheeler (Bike)",
        description = "High-frequency engine vibrations, handlebar tilt & severe road shocks",
        maxSpeedKmh = 80f,
        typicalVibrationLevel = "High (0.9 m/s²)"
    ),

    COMMERCIAL_TRUCK(
        displayName = "Truck / Heavy Vehicle",
        description = "Low-frequency heavy chassis oscillation, large turning radii",
        maxSpeedKmh = 70f,
        typicalVibrationLevel = "Heavy (0.6 m/s²)"
    )
}
