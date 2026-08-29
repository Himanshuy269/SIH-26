package com.isro.deadreckoning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.isro.deadreckoning.ui.navigation.AppNavGraph
import com.isro.deadreckoning.ui.theme.DeadReckoningTheme

/**
 * Single Activity hosting the Jetpack Compose UI.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as DeadReckoningApp).container

        setContent {
            DeadReckoningTheme {
                AppNavGraph(appContainer = appContainer)
            }
        }
    }
}
