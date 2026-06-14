package com.camerax.presentation.ui.camera.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.camerax.presentation.theme.GridLineColor

@Composable
fun GridOverlay(
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val strokeWidth = 1f

            // Vertical lines (rule of thirds)
            drawLine(
                color = GridLineColor,
                start = Offset(width / 3f, 0f),
                end = Offset(width / 3f, height),
                strokeWidth = strokeWidth,
            )
            drawLine(
                color = GridLineColor,
                start = Offset(2f * width / 3f, 0f),
                end = Offset(2f * width / 3f, height),
                strokeWidth = strokeWidth,
            )

            // Horizontal lines (rule of thirds)
            drawLine(
                color = GridLineColor,
                start = Offset(0f, height / 3f),
                end = Offset(width, height / 3f),
                strokeWidth = strokeWidth,
            )
            drawLine(
                color = GridLineColor,
                start = Offset(0f, 2f * height / 3f),
                end = Offset(width, 2f * height / 3f),
                strokeWidth = strokeWidth,
            )
        }
    }
}
