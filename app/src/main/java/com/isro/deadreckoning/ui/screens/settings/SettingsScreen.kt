package com.isro.deadreckoning.ui.screens.settings

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Speed
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.isro.deadreckoning.ui.theme.BackgroundDark
import com.isro.deadreckoning.ui.theme.IsroOrange
import com.isro.deadreckoning.ui.theme.NavicBlue
import com.isro.deadreckoning.ui.theme.StatusCyan
import com.isro.deadreckoning.ui.theme.StatusGreen
import com.isro.deadreckoning.ui.theme.StatusPurple
import com.isro.deadreckoning.ui.theme.SurfaceCard
import com.isro.deadreckoning.ui.theme.SurfaceCardBorder
import com.isro.deadreckoning.ui.theme.TextMuted
import com.isro.deadreckoning.ui.theme.TextPrimary

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SIH 2024 / ISRO",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = IsroOrange,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                )
                Text(
                    text = "Project Architecture & Info",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Problem Statement Card
        InfoCard(
            title = "Problem Statement Details",
            icon = Icons.Default.Info,
            iconColor = NavicBlue
        ) {
            InfoRow(label = "Department", value = "Indian Space Research Organisation (ISRO)")
            InfoRow(label = "Category", value = "Software / Smart Vehicles")
            InfoRow(label = "Dataset", value = "IO-VNBD (Inertial & Odometry Benchmark Dataset)")
            InfoRow(
                label = "Objective",
                value = "AI-ML based dead reckoning and GNSS+INS fusion for vehicles in GNSS-denied environments (tunnels, urban canyons, underpasses)."
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Performance Benchmarks Card
        InfoCard(
            title = "SIH Performance Benchmarks",
            icon = Icons.Default.Speed,
            iconColor = StatusGreen
        ) {
            InfoRow(label = "Mobile Update Rate", value = "10 Hz (Smartphone Application)")
            InfoRow(label = "Edge Engine Rate", value = "200 Hz (FOG / External IMU)")
            InfoRow(
                label = "Dead Reckoning Drift Target",
                value = "< 10% distance  (< 5m over 50m | < 100m over 1km at 60 km/h)"
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Future AI/ML Integration Slots Card
        InfoCard(
            title = "Future AI/ML Integration Slots",
            icon = Icons.Default.Memory,
            iconColor = StatusCyan
        ) {
            Text(
                text = "These interfaces are already wired — connect your trained IO-VNBD model:",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextMuted,
                    fontSize = 12.sp
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            ConfigToggleRow(
                title = "AI Speed & Vibration Filter",
                description = "Removes chassis vibrations, predicts forward velocity from IMU",
                isChecked = uiState.isAiFilterEnabled,
                onCheckedChange = { viewModel.toggleAiFilter(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            ConfigToggleRow(
                title = "Map-Matching Engine",
                description = "Snaps IMU trajectory to road geometry using OSM road database",
                isChecked = uiState.isMapMatchingEnabled,
                onCheckedChange = { viewModel.toggleMapMatching(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            ConfigToggleRow(
                title = "Non-Holonomic Constraints (NHC)",
                description = "Assumes car cannot slide sideways or fly upward — bounds drift",
                isChecked = uiState.isNonHolonomicConstraintEnabled,
                onCheckedChange = { viewModel.toggleNonHolonomicConstraints(it) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // AI Runtime Selector
            Text(
                text = "AI RUNTIME",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            AiRuntimeSelector(
                selected = uiState.selectedAiRuntime,
                onSelect = { viewModel.selectAiRuntime(it) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Clean Architecture Layers Card
        InfoCard(
            title = "Clean Architecture Layers",
            icon = Icons.Default.Layers,
            iconColor = IsroOrange
        ) {
            ArchitectureFlowRow(
                label = "UI Layer",
                value = "Jetpack Compose (Screens + Components)",
                color = NavicBlue
            )
            ArchitectureFlowRow(
                label = "ViewModel",
                value = "StateFlow + conflate() for smooth 10Hz updates",
                color = StatusCyan
            )
            ArchitectureFlowRow(
                label = "Repository",
                value = "NavigationRepository (orchestrates Engine + Sensors)",
                color = StatusGreen
            )
            ArchitectureFlowRow(
                label = "Engine Interface",
                value = "NavigationEngine → MockNavigationEngine | RealEngine (Future)",
                color = IsroOrange
            )
            ArchitectureFlowRow(
                label = "AI / INS / Sensors",
                value = "Pluggable: TFLite / EKF / SensorManager / LocationProvider",
                color = StatusPurple,
                isLast = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun AiRuntimeSelector(
    selected: String,
    onSelect: (String) -> Unit
) {
    val runtimes = listOf("TensorFlow Lite (LiteRT)", "ONNX Runtime", "PyTorch Mobile", "Native C++ (NDK)")
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        runtimes.forEach { runtime ->
            val isSelected = runtime == selected
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isSelected) NavicBlue.copy(alpha = 0.15f)
                        else SurfaceCardBorder.copy(alpha = 0.25f)
                    )
                    .border(1.dp, if (isSelected) NavicBlue.copy(alpha = 0.6f) else SurfaceCardBorder, RoundedCornerShape(8.dp))
                    .clickable { onSelect(runtime) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = runtime,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isSelected) TextPrimary else TextMuted,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                )
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = NavicBlue,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ArchitectureFlowRow(
    label: String,
    value: String,
    color: Color,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Left color indicator + connector line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(color)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(24.dp)
                        .background(color.copy(alpha = 0.3f))
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = color,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
            )
            Spacer(modifier = Modifier.height(if (isLast) 0.dp else 6.dp))
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    content: @Composable () -> Unit
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label.uppercase(),
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
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        )
    }
}

@Composable
private fun ConfigToggleRow(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceCardBorder.copy(alpha = 0.35f))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextMuted,
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            )
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NavicBlue,
                checkedTrackColor = NavicBlue.copy(alpha = 0.35f),
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = SurfaceCardBorder
            )
        )
    }
}
