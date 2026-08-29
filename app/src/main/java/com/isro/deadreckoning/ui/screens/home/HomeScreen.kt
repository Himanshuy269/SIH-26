package com.isro.deadreckoning.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.isro.deadreckoning.domain.model.NavigationMode
import com.isro.deadreckoning.domain.model.VehicleProfile
import com.isro.deadreckoning.ui.components.CoordinateCard
import com.isro.deadreckoning.ui.components.NavigationModeBadge
import com.isro.deadreckoning.ui.components.SpeedometerGauge
import com.isro.deadreckoning.ui.components.TrajectoryCanvas
import com.isro.deadreckoning.ui.theme.BackgroundDark
import com.isro.deadreckoning.ui.theme.IsroOrange
import com.isro.deadreckoning.ui.theme.NavicBlue
import com.isro.deadreckoning.ui.theme.StatusGreen
import com.isro.deadreckoning.ui.theme.StatusRed
import com.isro.deadreckoning.ui.theme.SurfaceCard
import com.isro.deadreckoning.ui.theme.SurfaceCardBorder
import com.isro.deadreckoning.ui.theme.TextMuted
import com.isro.deadreckoning.ui.theme.TextPrimary

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
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
        // 1. App Title & Status Indicator
        HeaderSection()

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Vehicle Profile Selector (Car / Two-Wheeler / Truck)
        VehicleProfileSelector(
            selectedProfile = uiState.vehicleProfile,
            onProfileSelected = { viewModel.selectVehicleProfile(it) }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Navigation Mode Badge
        NavigationModeBadge(mode = uiState.navigationMode)

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Center Speedometer Gauge (With Acceleration Readout)
        SpeedometerGauge(
            currentSpeedKmh = uiState.speedKmh,
            accelerationMps2 = uiState.accelerationMps2,
            maxSpeedKmh = uiState.vehicleProfile.maxSpeedKmh,
            accentColor = if (uiState.navigationMode == NavigationMode.GNSS_FIX) NavicBlue else IsroOrange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Live 2D Trajectory Radar / Mini-Map Canvas
        TrajectoryCanvas(
            currentLat = uiState.latitude,
            currentLon = uiState.longitude,
            bearingDegrees = uiState.bearingDegrees,
            isBlackoutActive = uiState.isBlackoutSimulated,
            driftRadiusMeters = uiState.driftEstimateMeters,
            trajectoryPoints = uiState.trajectoryHistory,
            onClearTrajectory = { viewModel.clearTrajectory() }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 6. Position & Coordinate Telemetry Card
        CoordinateCard(
            latitude = uiState.latitude,
            longitude = uiState.longitude,
            altitudeMeters = uiState.altitudeMeters,
            bearingDegrees = uiState.bearingDegrees,
            accuracyMeters = uiState.accuracyMeters,
            satellitesCount = uiState.satellitesCount
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 7. SIH Dead Reckoning Performance Metric Card (During Blackouts)
        if (uiState.isBlackoutSimulated) {
            DriftBenchmarkCard(
                driftMeters = uiState.driftEstimateMeters,
                driftPercentage = uiState.driftPercentage,
                distanceTraveled = uiState.distanceTraveledMeters
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 8. Demo & Testing Controls
        SimulationControlCard(
            isBlackoutActive = uiState.isBlackoutSimulated,
            onToggleBlackout = { viewModel.toggleBlackoutSimulation(it) },
            onRecalibrate = { viewModel.recalibratePhoneOrientation() }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "ISRO SMART VEHICLE",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = NavicBlue,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            )
            Text(
                text = "Intelligent Dead Reckoning",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceCard)
                .border(1.dp, SurfaceCardBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "10 Hz LIVE",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = StatusGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
private fun VehicleProfileSelector(
    selectedProfile: VehicleProfile,
    onProfileSelected: (VehicleProfile) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        VehicleChip(
            title = "Car",
            icon = Icons.Default.DirectionsCar,
            isSelected = selectedProfile == VehicleProfile.CAR,
            onClick = { onProfileSelected(VehicleProfile.CAR) },
            modifier = Modifier.weight(1f)
        )
        VehicleChip(
            title = "2-Wheeler",
            icon = Icons.Default.DirectionsBike,
            isSelected = selectedProfile == VehicleProfile.TWO_WHEELER,
            onClick = { onProfileSelected(VehicleProfile.TWO_WHEELER) },
            modifier = Modifier.weight(1f)
        )
        VehicleChip(
            title = "Truck",
            icon = Icons.Default.LocalShipping,
            isSelected = selectedProfile == VehicleProfile.COMMERCIAL_TRUCK,
            onClick = { onProfileSelected(VehicleProfile.COMMERCIAL_TRUCK) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun VehicleChip(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) NavicBlue else SurfaceCardBorder
    val bgColor = if (isSelected) NavicBlue.copy(alpha = 0.15f) else SurfaceCard

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) NavicBlue else TextMuted,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) TextPrimary else TextMuted,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
private fun DriftBenchmarkCard(
    driftMeters: Float,
    driftPercentage: Float,
    distanceTraveled: Float,
    modifier: Modifier = Modifier
) {
    val isWithinBenchmark = driftPercentage <= 10.0f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCard)
            .border(1.dp, if (isWithinBenchmark) StatusGreen.copy(alpha = 0.5f) else StatusRed.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
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
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Outage Warning",
                        tint = IsroOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SIH DRIFT BENCHMARK (< 10%)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = IsroOrange,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }

                Text(
                    text = if (isWithinBenchmark) "BENCHMARK MET" else "DRIFT EXCEEDED",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isWithinBenchmark) StatusGreen else StatusRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { (driftPercentage / 20f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isWithinBenchmark) StatusGreen else StatusRed,
                trackColor = SurfaceCardBorder,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = String.format("Drift: %.2f m (%.1f%%)", driftMeters, driftPercentage),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = String.format("Distance: %.1f m", distanceTraveled),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }
    }
}

@Composable
private fun SimulationControlCard(
    isBlackoutActive: Boolean,
    onToggleBlackout: (Boolean) -> Unit,
    onRecalibrate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = "DEMO & TESTING CONTROLS",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontSize = 10.sp,
                    letterSpacing = 1.2.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Blackout Simulation Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Simulate GNSS Blackout (Tunnel)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = "Forces switch to Dead Reckoning (INS + AI)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    )
                }

                Switch(
                    checked = isBlackoutActive,
                    onCheckedChange = onToggleBlackout,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = IsroOrange,
                        checkedTrackColor = IsroOrange.copy(alpha = 0.35f),
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = SurfaceCardBorder
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Calibration Button
            Button(
                onClick = onRecalibrate,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurfaceCardBorder.copy(alpha = 0.6f),
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Calibrate",
                    modifier = Modifier.size(16.dp),
                    tint = NavicBlue
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Recalibrate Phone Orientation",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}
