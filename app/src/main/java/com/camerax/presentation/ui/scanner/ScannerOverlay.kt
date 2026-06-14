package com.camerax.presentation.ui.scanner

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.camerax.presentation.theme.QrScannerGreen

@Composable
fun ScannerOverlay(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "scanLine")
    val scanLineProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "scanLineProgress",
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val scanBoxSize = (canvasWidth * 0.65f).coerceAtMost(canvasHeight * 0.4f)
        val left = (canvasWidth - scanBoxSize) / 2f
        val top = (canvasHeight - scanBoxSize) / 2f - canvasHeight * 0.05f

        // Dim background outside scan area
        val dimPath =
            Path().apply {
                addRect(Rect(0f, 0f, canvasWidth, canvasHeight))
                addRoundRect(
                    RoundRect(
                        left = left,
                        top = top,
                        right = left + scanBoxSize,
                        bottom = top + scanBoxSize,
                        cornerRadius = CornerRadius(16f, 16f),
                    ),
                )
            }
        drawPath(
            path = dimPath,
            color = Color.Black.copy(alpha = 0.55f),
        )

        // Corner brackets
        val bracketLength = scanBoxSize * 0.12f
        val bracketStroke = 4f
        val bracketColor = QrScannerGreen

        // Top-left
        drawLine(bracketColor, Offset(left, top + 16f), Offset(left, top + bracketLength), strokeWidth = bracketStroke)
        drawLine(bracketColor, Offset(left, top + 16f), Offset(left + bracketLength, top + 16f), strokeWidth = bracketStroke)

        // Top-right
        drawLine(
            bracketColor,
            Offset(left + scanBoxSize, top + 16f),
            Offset(left + scanBoxSize, top + bracketLength),
            strokeWidth = bracketStroke,
        )
        drawLine(
            bracketColor,
            Offset(left + scanBoxSize, top + 16f),
            Offset(left + scanBoxSize - bracketLength, top + 16f),
            strokeWidth = bracketStroke,
        )

        // Bottom-left
        drawLine(
            bracketColor,
            Offset(left, top + scanBoxSize - 16f),
            Offset(left, top + scanBoxSize - bracketLength),
            strokeWidth = bracketStroke,
        )
        drawLine(
            bracketColor,
            Offset(left, top + scanBoxSize - 16f),
            Offset(left + bracketLength, top + scanBoxSize - 16f),
            strokeWidth = bracketStroke,
        )

        // Bottom-right
        drawLine(
            bracketColor,
            Offset(left + scanBoxSize, top + scanBoxSize - 16f),
            Offset(left + scanBoxSize, top + scanBoxSize - bracketLength),
            strokeWidth = bracketStroke,
        )
        drawLine(
            bracketColor,
            Offset(left + scanBoxSize, top + scanBoxSize - 16f),
            Offset(left + scanBoxSize - bracketLength, top + scanBoxSize - 16f),
            strokeWidth = bracketStroke,
        )

        // Scan border
        drawRoundRect(
            color = QrScannerGreen.copy(alpha = 0.3f),
            topLeft = Offset(left, top),
            size = Size(scanBoxSize, scanBoxSize),
            cornerRadius = CornerRadius(16f, 16f),
            style = Stroke(width = 1.5f),
        )

        // Animated scan line
        val lineY = top + (scanBoxSize * scanLineProgress)
        drawLine(
            color = QrScannerGreen.copy(alpha = 0.8f),
            start = Offset(left + 8f, lineY),
            end = Offset(left + scanBoxSize - 8f, lineY),
            strokeWidth = 2f,
        )
    }
}
