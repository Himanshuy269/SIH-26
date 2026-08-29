package com.isro.deadreckoning

import android.app.Application
import com.isro.deadreckoning.core.di.AppContainer
import com.isro.deadreckoning.core.di.DefaultAppContainer

/**
 * Custom Application class that initializes the global [AppContainer] during app startup.
 */
class DeadReckoningApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
