package com.isro.deadreckoning.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.isro.deadreckoning.domain.model.NavigationMode
import com.isro.deadreckoning.domain.model.TrajectoryPoint
import com.isro.deadreckoning.ui.theme.IsroOrange
import com.isro.deadreckoning.ui.theme.NavicBlue
import com.isro.deadreckoning.ui.theme.StatusCyan
import com.isro.deadreckoning.ui.theme.StatusGreen
import com.isro.deadreckoning.ui.theme.SurfaceCard
import com.isro.deadreckoning.ui.theme.SurfaceCardBorder
import com.isro.deadreckoning.ui.theme.TextMuted
import com.isro.deadreckoning.ui.theme.TextPrimary
import kotlin.math.cos

/**
 * High-performance 2D Trajectory Radar / Mini-Map Canvas.
 *
 * Visualizes:
 * - Real-time vehicle path breadcrumbs relative to current position.
 * - Color transitions: NavIC Cyan for GNSS Fix ➔ ISRO Orange for Dead Reckoning.
 * - Rotating vehicle arrowhead pointing in the driving direction.
 * - Pulsing uncertainty drift radius during GNSS blackouts.
 * - Polar radar grid lines and range rings.
 */
@Composable
fun TrajectoryCanvas(
    currentLat: Double,
    currentLon: Double,
    bearingDegrees: Float,
    isBlackoutActive: Boolean,
    driftRadiusMeters: Float,
    trajectoryPoints: List<TrajectoryPoint>,
    onClearTrajectory: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 220.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "DriftPulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        // 1. Radar Canvas Layer
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = (minOf(size.width, size.height) / 2f) - 10.dp.toPx()

            // 1a. Radar concentric range rings (50m, 100m, 150m)
            val ringCount = 3
            for (i in 1..ringCount) {
                val r = (maxRadius / ringCount) * i
                drawCircle(
                    color = SurfaceCardBorder.copy(alpha = 0.45f),
                    radius = r,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // 1b. Crosshairs
            drawLine(
                color = SurfaceCardBorder.copy(alpha = 0.35f),
                start = Offset(center.x, center.y - maxRadius),
                end = Offset(center.x, center.y + maxRadius),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = SurfaceCardBorder.copy(alpha = 0.35f),
                start = Offset(center.x - maxRadius, center.y),
                end = Offset(center.x + maxRadius, center.y),
                strokeWidth = 1.dp.toPx()
            )

            // 1c. Plot Trajectory Breadcrumb Trail
            // Scale: maxRadius represents ~150 meters from vehicle
            val metersPerPixel = 150f / maxRadius
            val cosLat = cos(Math.toRadians(currentLat))

            if (trajectoryPoints.size > 1) {
                var prevOffset: Offset? = null

                for (point in trajectoryPoints) {
                    // Convert delta lat/lon to local Cartesian meters relative to current vehicle position
                    val deltaLatMeters = (point.latitude - currentLat) * 111_000.0
                    val deltaLonMeters = (point.longitude - currentLon) * (111_000.0 * cosLat)

                    // Convert to radar pixel offsets (North is -Y, East is +X)
                    val pxX = center.x + (deltaLonMeters / metersPerPixel).toFloat()
                    val pxY = center.y - (deltaLatMeters / metersPerPixel).toFloat()
                    val pointOffset = Offset(pxX, pxY)

                    val pointColor = when (point.mode) {
                        NavigationMode.GNSS_FIX, NavigationMode.GNSS_AIDED_INS -> StatusCyan
                        NavigationMode.DEAD_RECKONING_INS, NavigationMode.AI_ENHANCED_FUSION -> IsroOrange
                        else -> NavicBlue
                    }

                    // Draw connection line
                    prevOffset?.let { pOffset ->
                        drawLine(
                            color = pointColor.copy(alpha = 0.7f),
                            start = pOffset,
                            end = pointOffset,
                            strokeWidth = 2.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // Draw breadcrumb dot
                    drawCircle(
                        color = pointColor,
                        radius = 2.5.dp.toPx(),
                        center = pointOffset
                    )

                    prevOffset = pointOffset
                }
            }

            // 1d. If in GNSS Blackout, draw dynamic drift uncertainty circle
            if (isBlackoutActive && driftRadiusMeters > 0f) {
                val driftRadiusPx = (driftRadiusMeters / metersPerPixel).coerceIn(8.dp.toPx(), maxRadius * 0.8f)
                drawCircle(
                    color = IsroOrange.copy(alpha = 0.15f),
                    radius = driftRadiusPx * pulseScale,
                    center = center
                )
                drawCircle(
                    color = IsroOrange.copy(alpha = 0.5f),
                    radius = driftRadiusPx,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            // 1e. Vehicle Arrow Cursor at Center (Oriented with Bearing)
            rotate(degrees = bearingDegrees, pivot = center) {
                val arrowPath = Path().apply {
                    moveTo(center.x, center.y - 12.dp.toPx()) // Tip
                    lineTo(center.x + 8.dp.toPx(), center.y + 9.dp.toPx()) // Right wing
                    lineTo(center.x, center.y + 4.dp.toPx()) // Inner notch
                    lineTo(center.x - 8.dp.toPx(), center.y + 9.dp.toPx()) // Left wing
                    close()
                }

                val cursorColor = if (isBlackoutActive) IsroOrange else NavicBlue

                drawPath(
                    path = arrowPath,
                    color = cursorColor
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.dp.toPx(),
                    center = center
                )
            }
        }

        // 2. Overlay Labels & Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "TRAJECTORY RADAR",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = NavicBlue,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = if (isBlackoutActive) "INS DEAD RECKONING PATH" else "GNSS SATELLITE TRACK",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isBlackoutActive) IsroOrange else StatusGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
            }

            // Clear Path Button
            IconButton(
                onClick = onClearTrajectory,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Clear Trajectory Trail",
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // 3. Bottom Range Scale Indicator
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .clip(RoundedCornerShape(6.dp))
                .background(SurfaceCardBorder.copy(alpha = 0.5f))
                .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isBlackoutActive) IsroOrange else StatusCyan)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "150m Range",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontSize = 9.sp
                )
            )
        }
    }
}
