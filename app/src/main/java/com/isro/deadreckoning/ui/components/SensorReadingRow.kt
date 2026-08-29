package com.isro.deadreckoning.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.isro.deadreckoning.ui.theme.IsroOrange
import com.isro.deadreckoning.ui.theme.NavicBlue
import com.isro.deadreckoning.ui.theme.StatusGreen
import com.isro.deadreckoning.ui.theme.StatusRed
import com.isro.deadreckoning.ui.theme.SurfaceCard
import com.isro.deadreckoning.ui.theme.SurfaceCardBorder
import com.isro.deadreckoning.ui.theme.TextMuted
import com.isro.deadreckoning.ui.theme.TextPrimary
import kotlin.math.abs

/**
 * Enhanced 3-Axis sensor reading card with real-time deflection visualizer meters.
 *
 * Demonstrates:
 * - Digital values for X, Y, Z axes.
 * - Dynamic zero-centered bar deflection graphs showing vibration/oscillation magnitude.
 * - Color shifting based on shock/vibration intensity.
 */
@Composable
fun SensorReadingCard(
    sensorTitle: String,
    unit: String,
    xValue: String,
    yValue: String,
    zValue: String,
    xRaw: Float = 0f,
    yRaw: Float = 0f,
    zRaw: Float = 0f,
    maxRange: Float = 15f,
    modifier: Modifier = Modifier,
    statusText: String = "NOT CONNECTED",
    statusColor: Color = TextMuted
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = sensorTitle.uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "($unit)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    )
                }

                // Connection badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusColor.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = statusColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // X, Y, Z Value Columns with Deflection Visualizers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AxisDeflectionTile(axis = "X", value = xValue, raw = xRaw, maxRange = maxRange, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                AxisDeflectionTile(axis = "Y", value = yValue, raw = yRaw, maxRange = maxRange, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                AxisDeflectionTile(axis = "Z", value = zValue, raw = zRaw, maxRange = maxRange, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun AxisDeflectionTile(
    axis: String,
    value: String,
    raw: Float,
    maxRange: Float,
    modifier: Modifier = Modifier
) {
    val animatedRaw by animateFloatAsState(
        targetValue = raw,
        animationSpec = tween(durationMillis = 80),
        label = "DeflectionAnimation"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceCardBorder.copy(alpha = 0.35f))
            .padding(horizontal = 6.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "AXIS $axis",
            style = MaterialTheme.typography.labelSmall.copy(
                color = NavicBlue,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Center-zero bidirectional deflection bar
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(SurfaceCardBorder)
        ) {
            val centerBarX = size.width / 2f
            val fraction = (animatedRaw / maxRange).coerceIn(-1f, 1f)
            val barWidth = (size.width / 2f) * abs(fraction)

            val barColor = when {
                abs(fraction) > 0.75f -> StatusRed
                abs(fraction) > 0.4f -> IsroOrange
                else -> StatusGreen
            }

            if (fraction >= 0f) {
                // Deflection to right
                drawRect(
                    color = barColor,
                    topLeft = Offset(centerBarX, 0f),
                    size = Size(barWidth, size.height)
                )
            } else {
                // Deflection to left
                drawRect(
                    color = barColor,
                    topLeft = Offset(centerBarX - barWidth, 0f),
                    size = Size(barWidth, size.height)
                )
            }

            // Center notch line
            drawLine(
                color = Color.White.copy(alpha = 0.6f),
                start = Offset(centerBarX, 0f),
                end = Offset(centerBarX, size.height),
                strokeWidth = 1.5.dp.toPx()
            )
        }
    }
}
