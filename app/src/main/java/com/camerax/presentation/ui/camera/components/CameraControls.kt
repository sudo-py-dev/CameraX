package com.camerax.presentation.ui.camera.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.HdrOff
import androidx.compose.material.icons.filled.HdrOn
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Timer10
import androidx.compose.material.icons.filled.Timer3
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.camerax.R
import com.camerax.domain.model.CameraMode
import com.camerax.domain.model.FlashMode
import com.camerax.domain.model.TimerDelay

@Composable
fun TopCameraControls(
    flashMode: FlashMode,
    timerDelay: TimerDelay,
    gridEnabled: Boolean,
    hdrEnabled: Boolean,
    cameraMode: CameraMode,
    onFlashToggle: () -> Unit,
    onTimerToggle: () -> Unit,
    onGridToggle: () -> Unit,
    onHdrToggle: () -> Unit,
    onFlipCamera: () -> Unit,
    flipRotation: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Flash
        IconButton(onClick = onFlashToggle) {
            Icon(
                imageVector =
                    when (flashMode) {
                        FlashMode.AUTO -> Icons.Default.FlashAuto
                        FlashMode.ON -> Icons.Default.FlashOn
                        FlashMode.OFF -> Icons.Default.FlashOff
                        FlashMode.TORCH -> Icons.Default.FlashOn
                    },
                contentDescription = stringResource(R.string.flash_toggle),
                tint = if (flashMode != FlashMode.OFF) Color(0xFFFFD54F) else Color.White,
                modifier = Modifier.size(26.dp),
            )
        }

        if (cameraMode == CameraMode.PHOTO) {
            // Timer
            IconButton(onClick = onTimerToggle) {
                Icon(
                    imageVector =
                        when (timerDelay) {
                            TimerDelay.OFF -> Icons.Default.TimerOff
                            TimerDelay.THREE -> Icons.Default.Timer3
                            TimerDelay.FIVE -> Icons.Default.Timer
                            TimerDelay.TEN -> Icons.Default.Timer10
                        },
                    contentDescription = stringResource(R.string.timer_toggle),
                    tint = if (timerDelay != TimerDelay.OFF) Color(0xFFFFD54F) else Color.White,
                    modifier = Modifier.size(26.dp),
                )
            }

            // HDR
            IconButton(onClick = onHdrToggle) {
                Icon(
                    imageVector = if (hdrEnabled) Icons.Default.HdrOn else Icons.Default.HdrOff,
                    contentDescription = stringResource(R.string.hdr_toggle),
                    tint = if (hdrEnabled) Color(0xFFFFD54F) else Color.White,
                    modifier = Modifier.size(26.dp),
                )
            }
        }

        // Grid
        IconButton(onClick = onGridToggle) {
            Icon(
                imageVector = Icons.Default.GridOn,
                contentDescription = stringResource(R.string.grid_toggle),
                tint = if (gridEnabled) Color(0xFFFFD54F) else Color.White,
                modifier = Modifier.size(26.dp),
            )
        }

        // Camera Flip
        val animatedRotation by animateFloatAsState(
            targetValue = flipRotation,
            animationSpec = tween(400),
            label = "flipRotation",
        )

        IconButton(onClick = onFlipCamera) {
            Icon(
                imageVector = Icons.Default.Cameraswitch,
                contentDescription = stringResource(R.string.flip_camera),
                tint = Color.White,
                modifier =
                    Modifier
                        .size(26.dp)
                        .rotate(animatedRotation),
            )
        }
    }
}

@Composable
fun ModeSelector(
    selectedMode: CameraMode,
    onModeSelected: (CameraMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(24.dp))
                .background(Color.Black.copy(alpha = 0.4f))
                .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CameraMode.entries.forEach { mode ->
            val isSelected = mode == selectedMode
            val label =
                when (mode) {
                    CameraMode.PHOTO -> stringResource(R.string.mode_photo)
                    CameraMode.VIDEO -> stringResource(R.string.mode_video)
                    CameraMode.QR_SCANNER -> stringResource(R.string.mode_qr)
                }
            Box(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                            } else {
                                Color.Transparent
                            },
                        )
                        .clickable { onModeSelected(mode) }
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                    style =
                        MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                        ),
                )
            }
        }
    }
}
