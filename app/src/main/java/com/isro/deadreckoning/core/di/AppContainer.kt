package com.isro.deadreckoning.core.di

import android.content.Context
import com.isro.deadreckoning.data.mock.MockLocationDataSource
import com.isro.deadreckoning.data.mock.MockNavigationEngine
import com.isro.deadreckoning.data.mock.MockSensorDataSource
import com.isro.deadreckoning.data.repository.NavigationRepositoryImpl
import com.isro.deadreckoning.domain.engine.LocationDataSource
import com.isro.deadreckoning.domain.engine.NavigationEngine
import com.isro.deadreckoning.domain.engine.SensorDataSource
import com.isro.deadreckoning.domain.repository.NavigationRepository

/**
 * Clean Manual Dependency Injection (DI) Container / Service Locator.
 *
 * HOW IT WORKS & WHY WE USE IT:
 * 1. Instead of creating objects inside ViewModels or Screens with `MockNavigationEngine()`,
 *    every component requests its dependency from this container.
 * 2. When your team implements the real Android Sensor APIs (SensorManager) or AI model (TFLite/INS),
 *    you ONLY change the instantiation line here in this file!
 * 3. Zero changes will be needed in any UI composables or ViewModels.
 */
interface AppContainer {
    val navigationEngine: NavigationEngine
    val sensorDataSource: SensorDataSource
    val locationDataSource: LocationDataSource
    val navigationRepository: NavigationRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    // =========================================================================
    // 1. ENGINE INSTANTIATION (Mock vs Real)
    // =========================================================================
    // When real AI/INS sensor fusion engine is ready:
    // override val navigationEngine: NavigationEngine by lazy {
    //     RealDeadReckoningEngine(aiModelRunner, insFilter, mapMatcher)
    // }
    override val navigationEngine: NavigationEngine by lazy {
        MockNavigationEngine()
    }

    // =========================================================================
    // 2. SENSOR DATA SOURCE (Mock vs Android Hardware)
    // =========================================================================
    // When Android SensorManager implementation is ready:
    // override val sensorDataSource: SensorDataSource by lazy {
    //     AndroidSensorDataSource(context)
    // }
    override val sensorDataSource: SensorDataSource by lazy {
        MockSensorDataSource()
    }

    // =========================================================================
    // 3. LOCATION DATA SOURCE (Mock vs FusedLocationProvider)
    // =========================================================================
    // When GPS/NavIC location provider is ready:
    // override val locationDataSource: LocationDataSource by lazy {
    //     AndroidLocationDataSource(context)
    // }
    override val locationDataSource: LocationDataSource by lazy {
        MockLocationDataSource()
    }

    // =========================================================================
    // 4. NAVIGATION REPOSITORY (Single source of truth)
    // =========================================================================
    override val navigationRepository: NavigationRepository by lazy {
        NavigationRepositoryImpl(
            navigationEngine = navigationEngine,
            sensorDataSource = sensorDataSource,
            locationDataSource = locationDataSource
        )
    }
}
