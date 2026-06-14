package com.camerax.presentation.ui.camera

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.camerax.R
import com.camerax.domain.model.CameraMode
import com.camerax.presentation.theme.RecordingRed
import com.camerax.presentation.ui.camera.components.CameraPreview
import com.camerax.presentation.ui.camera.components.CaptureButton
import com.camerax.presentation.ui.camera.components.GridOverlay
import com.camerax.presentation.ui.camera.components.ModeSelector
import com.camerax.presentation.ui.camera.components.TimerOverlay
import com.camerax.presentation.ui.camera.components.TopCameraControls
import com.camerax.presentation.ui.camera.components.ZoomController
import com.camerax.presentation.ui.scanner.ScanResultSheet
import com.camerax.presentation.ui.scanner.ScannerOverlay
import com.camerax.presentation.ui.util.FormatUtils

@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    onNavigateToGallery: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraMode by viewModel.cameraMode.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val recordingDuration by viewModel.recordingDurationMs.collectAsState()
    val zoomRatio by viewModel.zoomRatio.collectAsState()
    val minZoom by viewModel.minZoom.collectAsState()
    val maxZoom by viewModel.maxZoom.collectAsState()
    val showZoomSlider by viewModel.showZoomSlider.collectAsState()
    val flipRotation by viewModel.flipRotation.collectAsState()
    val countdownValue by viewModel.countdownValue.collectAsState()
    val isCountingDown by viewModel.isCountingDown.collectAsState()
    val lastCapturedUri by viewModel.lastCapturedUri.collectAsState()
    val scanResult by viewModel.scanResult.collectAsState()
    val captureFlash by viewModel.captureFlash.collectAsState()
    val flashMode by viewModel.flashMode.collectAsState()
    val timerDelay by viewModel.timerDelay.collectAsState()
    val gridEnabled by viewModel.gridEnabled.collectAsState()
    val hdrEnabled by viewModel.hdrEnabled.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startCamera(context, lifecycleOwner)
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Black),
    ) {
        // Camera Preview
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            onPreviewViewReady = { viewModel.onPreviewViewReady(it) },
            onTap = { x, y -> viewModel.onTapToFocus(x, y) },
            onPinchZoom = { scale -> viewModel.onPinchZoom(scale) },
        )

        // Grid Overlay
        GridOverlay(
            visible = gridEnabled,
            modifier = Modifier.fillMaxSize(),
        )

        // Scanner Overlay (QR mode only)
        if (cameraMode == CameraMode.QR_SCANNER) {
            ScannerOverlay(modifier = Modifier.fillMaxSize())
        }

        // Timer Countdown Overlay
        TimerOverlay(
            countdownValue = countdownValue,
            visible = isCountingDown,
        )

        // Capture Flash effect
        AnimatedVisibility(
            visible = captureFlash,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.7f)),
            )
        }

        // Top Controls
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars),
        ) {
            TopCameraControls(
                flashMode = flashMode,
                timerDelay = timerDelay,
                gridEnabled = gridEnabled,
                hdrEnabled = hdrEnabled,
                cameraMode = cameraMode,
                onFlashToggle = { viewModel.toggleFlash() },
                onTimerToggle = { viewModel.toggleTimer() },
                onGridToggle = { viewModel.toggleGrid() },
                onHdrToggle = { viewModel.toggleHdr() },
                onFlipCamera = { viewModel.flipCamera(context, lifecycleOwner) },
                flipRotation = flipRotation,
            )

            Spacer(modifier = Modifier.weight(1f))

            // Recording duration indicator
            AnimatedVisibility(
                visible = isRecording,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(RecordingRed),
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = FormatUtils.formatRecordingTimer(recordingDuration),
                        color = Color.White,
                        style =
                            MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Zoom Controller
            ZoomController(
                zoomRatio = zoomRatio,
                minZoom = minZoom,
                maxZoom = maxZoom,
                onZoomChanged = { viewModel.setZoomRatio(it) },
                visible = showZoomSlider,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mode Selector
            ModeSelector(
                selectedMode = cameraMode,
                onModeSelected = { mode ->
                    viewModel.setCameraMode(mode, context, lifecycleOwner)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Controls: Thumbnail | Capture | Gallery
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 32.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Last captured thumbnail
                Box(
                    modifier =
                        Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable { onNavigateToGallery() },
                    contentAlignment = Alignment.Center,
                ) {
                    if (lastCapturedUri != null) {
                        AsyncImage(
                            model =
                                ImageRequest.Builder(context)
                                    .data(Uri.parse(lastCapturedUri))
                                    .crossfade(true)
                                    .build(),
                            contentDescription = stringResource(R.string.last_capture),
                            contentScale = ContentScale.Crop,
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp)),
                        )
                    }
                }

                // Capture Button
                CaptureButton(
                    cameraMode = cameraMode,
                    isRecording = isRecording,
                    onClick = { viewModel.onCaptureAction(context) },
                )

                // Placeholder for symmetry
                Spacer(modifier = Modifier.size(48.dp))
            }
        }

        // Scan Result Sheet
        if (scanResult != null) {
            ScanResultSheet(
                result = scanResult,
                onDismiss = { viewModel.clearScanResult() },
            )
        }
    }
}
