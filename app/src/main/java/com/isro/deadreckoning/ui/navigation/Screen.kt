package com.isro.deadreckoning.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed class defining the top-level navigation routes in the application.
 */
sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Home : Screen(
        route = "home",
        title = "Navigation HUD",
        icon = Icons.Default.Navigation
    )

    data object Sensors : Screen(
        route = "sensors",
        title = "Sensors",
        icon = Icons.Default.Sensors
    )

    data object Settings : Screen(
        route = "settings",
        title = "About & Config",
        icon = Icons.Default.Info
    )
}
