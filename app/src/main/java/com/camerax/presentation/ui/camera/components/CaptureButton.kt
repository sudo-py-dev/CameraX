package com.camerax.presentation.ui.camera.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.camerax.domain.model.CameraMode
import com.camerax.presentation.theme.QrScannerGreen
import com.camerax.presentation.theme.RecordingRed

@Composable
fun CaptureButton(
    cameraMode: CameraMode,
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val outerColor by animateColorAsState(
        targetValue =
            when {
                isRecording -> RecordingRed
                cameraMode == CameraMode.VIDEO -> RecordingRed.copy(alpha = 0.7f)
                cameraMode == CameraMode.QR_SCANNER -> QrScannerGreen
                else -> Color.White
            },
        animationSpec = tween(300),
        label = "outerColor",
    )

    val innerColor by animateColorAsState(
        targetValue =
            when {
                isRecording -> RecordingRed
                cameraMode == CameraMode.VIDEO -> Color.White
                cameraMode == CameraMode.QR_SCANNER -> QrScannerGreen
                else -> Color.White
            },
        animationSpec = tween(300),
        label = "innerColor",
    )

    val infiniteTransition = rememberInfiniteTransition(label = "recordPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.15f else 1f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(800),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "pulseScale",
    )

    val buttonSize = 72.dp
    val innerSize = if (isRecording) 28.dp else 56.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .size(buttonSize * pulseScale)
                .clip(CircleShape)
                .border(4.dp, outerColor, CircleShape)
                .clickable(onClick = onClick)
                .semantics {
                    contentDescription =
                        when (cameraMode) {
                            CameraMode.PHOTO -> "Capture photo"
                            CameraMode.VIDEO -> if (isRecording) "Stop recording" else "Start recording"
                            CameraMode.QR_SCANNER -> "Scanner active"
                        }
                },
    ) {
        Canvas(
            modifier = Modifier.size(innerSize),
        ) {
            if (isRecording) {
                val cornerRadius = size.minDimension * 0.2f
                drawRoundRect(
                    color = innerColor,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius),
                )
            } else {
                drawCircle(color = innerColor)
            }
        }
    }
}
