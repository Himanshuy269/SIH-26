package com.isro.deadreckoning.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.isro.deadreckoning.ui.theme.NavicBlue
import com.isro.deadreckoning.ui.theme.StatusGreen
import com.isro.deadreckoning.ui.theme.SurfaceCard
import com.isro.deadreckoning.ui.theme.SurfaceCardBorder
import com.isro.deadreckoning.ui.theme.TextMuted
import com.isro.deadreckoning.ui.theme.TextPrimary

/**
 * Clean card displaying real-time vehicle positioning telemetry:
 * - Latitude / Longitude
 * - Altitude
 * - Heading / Bearing with rotating compass arrow
 * - Estimated Accuracy & Satellite Count
 */
@Composable
fun CoordinateCard(
    latitude: Double,
    longitude: Double,
    altitudeMeters: Double,
    bearingDegrees: Float,
    accuracyMeters: Float,
    satellitesCount: Int,
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
                text = "POSITION TELEMETRY",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontSize = 10.sp,
                    letterSpacing = 1.2.sp
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Row 1: Latitude & Longitude
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TelemetryTile(
                    label = "LATITUDE",
                    value = String.format("%.6f°", latitude),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                TelemetryTile(
                    label = "LONGITUDE",
                    value = String.format("%.6f°", longitude),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: Altitude, Heading & Accuracy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TelemetryTile(
                    label = "ALTITUDE",
                    value = String.format("%.1f m", altitudeMeters),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                TelemetryTileWithBearing(
                    label = "HEADING",
                    bearingDegrees = bearingDegrees,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                TelemetryTile(
                    label = "ACCURACY",
                    value = String.format("±%.1fm (%dsat)", accuracyMeters, satellitesCount),
                    valueColor = if (accuracyMeters < 3f) StatusGreen else NavicBlue,
                    modifier = Modifier.weight(1.2f)
                )
            }
        }
    }
}

@Composable
private fun TelemetryTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: androidx.compose.ui.graphics.Color = TextPrimary
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceCardBorder.copy(alpha = 0.35f))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextMuted,
                fontSize = 9.sp,
                letterSpacing = 0.8.sp
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = valueColor,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun TelemetryTileWithBearing(
    label: String,
    bearingDegrees: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceCardBorder.copy(alpha = 0.35f))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextMuted,
                fontSize = 9.sp,
                letterSpacing = 0.8.sp
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Navigation,
                contentDescription = "Bearing Arrow",
                tint = NavicBlue,
                modifier = Modifier
                    .size(12.dp)
                    .rotate(bearingDegrees)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = String.format("%.0f°", bearingDegrees),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            )
        }
    }
}
