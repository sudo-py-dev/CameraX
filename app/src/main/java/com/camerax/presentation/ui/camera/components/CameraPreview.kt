package com.camerax.presentation.ui.camera.components

import androidx.camera.view.PreviewView
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onPreviewViewReady: (PreviewView) -> Unit,
    onTap: (Float, Float) -> Unit = { _, _ -> },
    onPinchZoom: (Float) -> Unit = {},
) {
    AndroidView(
        factory = { context ->
            PreviewView(context).apply {
                implementationMode = PreviewView.ImplementationMode.PERFORMANCE
                scaleType = PreviewView.ScaleType.FILL_CENTER
                onPreviewViewReady(this)
            }
        },
        modifier =
            modifier
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        onTap(offset.x, offset.y)
                    }
                }
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        onPinchZoom(zoom)
                    }
                },
    )
}
