package com.isro.deadreckoning.ui.screens.sensors

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Satellite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.isro.deadreckoning.ui.components.SensorReadingCard
import com.isro.deadreckoning.ui.theme.BackgroundDark
import com.isro.deadreckoning.ui.theme.NavicBlue
import com.isro.deadreckoning.ui.theme.StatusGreen
import com.isro.deadreckoning.ui.theme.SurfaceCard
import com.isro.deadreckoning.ui.theme.SurfaceCardBorder
import com.isro.deadreckoning.ui.theme.TextMuted
import com.isro.deadreckoning.ui.theme.TextPrimary

@Composable
fun SensorDebugScreen(
    viewModel: SensorDebugViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "HARDWARE TELEMETRY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = NavicBlue,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                )
                Text(
                    text = "Sensor Debug & Feeds",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Info Banner
        InfoBannerCard(message = uiState.infoMessage)

        Spacer(modifier = Modifier.height(14.dp))

        // 1. Accelerometer Card with deflection bars
        SensorReadingCard(
            sensorTitle = "Accelerometer",
            unit = "m/s²",
            xValue = uiState.accelX,
            yValue = uiState.accelY,
            zValue = uiState.accelZ,
            xRaw = uiState.accelXRaw,
            yRaw = uiState.accelYRaw,
            zRaw = uiState.accelZRaw,
            maxRange = 15f,
            statusText = uiState.accelStatus,
            statusColor = if (uiState.isMockStreamEnabled) StatusGreen else TextMuted
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Gyroscope Card with deflection bars (smaller range: rad/s)
        SensorReadingCard(
            sensorTitle = "Gyroscope",
            unit = "rad/s",
            xValue = uiState.gyroX,
            yValue = uiState.gyroY,
            zValue = uiState.gyroZ,
            xRaw = uiState.gyroXRaw,
            yRaw = uiState.gyroYRaw,
            zRaw = uiState.gyroZRaw,
            maxRange = 1f,
            statusText = uiState.gyroStatus,
            statusColor = if (uiState.isMockStreamEnabled) StatusGreen else TextMuted
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Magnetometer Card with deflection bars (μT range ~100)
        SensorReadingCard(
            sensorTitle = "Magnetometer",
            unit = "μT",
            xValue = uiState.magX,
            yValue = uiState.magY,
            zValue = uiState.magZ,
            xRaw = uiState.magXRaw,
            yRaw = uiState.magYRaw,
            zRaw = uiState.magZRaw,
            maxRange = 80f,
            statusText = uiState.magStatus,
            statusColor = if (uiState.isMockStreamEnabled) StatusGreen else TextMuted
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 4. GNSS Raw Fix Card
        GnssDebugCard(
            latitude = uiState.gnssLat,
            longitude = uiState.gnssLon,
            speed = uiState.gnssSpeed,
            accuracy = uiState.gnssAccuracy,
            status = uiState.gnssStatus,
            isMockEnabled = uiState.isMockStreamEnabled
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 5. Mock stream toggle
        MockStreamToggleCard(
            isEnabled = uiState.isMockStreamEnabled,
            onToggle = { viewModel.toggleMockStream(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun InfoBannerCard(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .border(1.dp, NavicBlue.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info",
                tint = NavicBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextPrimary,
                    fontSize = 12.sp
                )
            )
        }
    }
}

@Composable
private fun GnssDebugCard(
    latitude: String,
    longitude: String,
    speed: String,
    accuracy: String,
    status: String,
    isMockEnabled: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Satellite,
                        contentDescription = "GNSS",
                        tint = if (isMockEnabled) StatusGreen else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GNSS / SATELLITE FIX",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                }

                Text(
                    text = status,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isMockEnabled) StatusGreen else TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GnssFieldTile("LATITUDE", latitude, Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                GnssFieldTile("LONGITUDE", longitude, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GnssFieldTile("SPEED", speed, Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                GnssFieldTile("ACCURACY", accuracy, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun GnssFieldTile(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceCardBorder.copy(alpha = 0.35f))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = NavicBlue,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun MockStreamToggleCard(
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Inspect Simulated Sensor Stream",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = "Toggle between '--' and simulated 50Hz IMU + deflection meters",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                )
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = NavicBlue,
                    checkedTrackColor = NavicBlue.copy(alpha = 0.35f),
                    uncheckedThumbColor = TextMuted,
                    uncheckedTrackColor = SurfaceCardBorder
                )
            )
        }
    }
}
