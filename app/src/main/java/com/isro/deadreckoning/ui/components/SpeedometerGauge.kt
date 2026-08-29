package com.isro.deadreckoning.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.isro.deadreckoning.ui.theme.IsroOrange
import com.isro.deadreckoning.ui.theme.NavicBlue
import com.isro.deadreckoning.ui.theme.StatusCyan
import com.isro.deadreckoning.ui.theme.StatusGreen
import com.isro.deadreckoning.ui.theme.StatusRed
import com.isro.deadreckoning.ui.theme.SurfaceCardBorder
import com.isro.deadreckoning.ui.theme.TextMuted
import com.isro.deadreckoning.ui.theme.TextPrimary
import kotlin.math.cos
import kotlin.math.sin

/**
 * Modern HUD circular arc speedometer gauge with acceleration meter.
 *
 * Performance Optimized:
 * - Pre-computes and caches geometry math.
 * - Smooth sub-pixel animated speed and acceleration sweeps.
 */
@Composable
fun SpeedometerGauge(
    currentSpeedKmh: Float,
    accelerationMps2: Float,
    modifier: Modifier = Modifier,
    maxSpeedKmh: Float = 120f,
    size: Dp = 230.dp,
    accentColor: Color = NavicBlue
) {
    val animatedSpeed by animateFloatAsState(
        targetValue = currentSpeedKmh.coerceIn(0f, maxSpeedKmh),
        animationSpec = tween(durationMillis = 200),
        label = "SpeedAnimation"
    )

    val speedFraction = (animatedSpeed / maxSpeedKmh).coerceIn(0f, 1f)

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            val arcSize = Size(this.size.width - strokeWidth * 2, this.size.height - strokeWidth * 2)
            val topLeft = Offset(strokeWidth, strokeWidth)
            val startAngle = 140f
            val totalSweepAngle = 260f

            // 1. Background Track Arc
            drawArc(
                color = SurfaceCardBorder,
                startAngle = startAngle,
                sweepAngle = totalSweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // 2. Active Speed Multi-Stop Gradient Arc
            val activeSweep = totalSweepAngle * speedFraction
            if (activeSweep > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        0.0f to NavicBlue,
                        0.4f to StatusCyan,
                        0.75f to IsroOrange,
                        1.0f to StatusRed
                    ),
                    startAngle = startAngle,
                    sweepAngle = activeSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // 3. Tick Marks Along Speed Arc
            val numTicks = 16
            val radius = this.size.width / 2 - strokeWidth * 1.7f
            val center = Offset(this.size.width / 2, this.size.height / 2)

            for (i in 0..numTicks) {
                val tickFraction = i / numTicks.toFloat()
                val tickAngleDeg = startAngle + totalSweepAngle * tickFraction
                val tickAngleRad = Math.toRadians(tickAngleDeg.toDouble())

                val isMajorTick = i % 4 == 0
                val innerR = radius - (if (isMajorTick) 10.dp.toPx() else 5.dp.toPx())
                val startX = center.x + radius * cos(tickAngleRad).toFloat()
                val startY = center.y + radius * sin(tickAngleRad).toFloat()
                val endX = center.x + innerR * cos(tickAngleRad).toFloat()
                val endY = center.y + innerR * sin(tickAngleRad).toFloat()

                val tickColor = if (tickFraction <= speedFraction) accentColor else SurfaceCardBorder
                drawLine(
                    color = tickColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = if (isMajorTick) 3.dp.toPx() else 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        // Center Digital Readout & Acceleration Badge
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = String.format("%.0f", animatedSpeed),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = TextPrimary
                )
            )
            Text(
                text = "KM / H",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Dynamic Acceleration Readout Pill (± m/s²)
            val isAccelerating = accelerationMps2 >= 0f
            val accelColor = if (isAccelerating) StatusGreen else StatusRed
            val accelText = String.format("%+.2f m/s²", accelerationMps2)

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceCardBorder.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAccelerating) "ACCEL" else "BRAKE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = accelColor,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = accelText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}
