package com.isro.deadreckoning.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.isro.deadreckoning.core.di.AppContainer
import com.isro.deadreckoning.ui.screens.home.HomeScreen
import com.isro.deadreckoning.ui.screens.home.HomeViewModel
import com.isro.deadreckoning.ui.screens.sensors.SensorDebugScreen
import com.isro.deadreckoning.ui.screens.sensors.SensorDebugViewModel
import com.isro.deadreckoning.ui.screens.settings.SettingsScreen
import com.isro.deadreckoning.ui.screens.settings.SettingsViewModel
import com.isro.deadreckoning.ui.theme.BackgroundDark
import com.isro.deadreckoning.ui.theme.NavicBlue
import com.isro.deadreckoning.ui.theme.SurfaceCard
import com.isro.deadreckoning.ui.theme.TextMuted
import com.isro.deadreckoning.ui.theme.TextPrimary

@Composable
fun AppNavGraph(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val items = listOf(
        Screen.Home,
        Screen.Sensors,
        Screen.Settings
    )

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceCard,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { screen ->
                    val selected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 11.sp
                            )
                        },
                        selected = selected,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavicBlue,
                            selectedTextColor = NavicBlue,
                            indicatorColor = NavicBlue.copy(alpha = 0.15f),
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.provideFactory(appContainer.navigationRepository)
                )
                HomeScreen(viewModel = homeViewModel)
            }

            composable(Screen.Sensors.route) {
                val sensorViewModel: SensorDebugViewModel = viewModel(
                    factory = SensorDebugViewModel.provideFactory(appContainer.navigationRepository)
                )
                SensorDebugScreen(viewModel = sensorViewModel)
            }

            composable(Screen.Settings.route) {
                val settingsViewModel: SettingsViewModel = viewModel()
                SettingsScreen(viewModel = settingsViewModel)
            }
        }
    }
}
