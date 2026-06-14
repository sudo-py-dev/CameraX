package com.camerax.presentation.ui.camera

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camerax.domain.model.BarcodeMapper
import com.camerax.domain.model.CameraMode
import com.camerax.domain.model.FlashMode
import com.camerax.domain.model.MediaItem
import com.camerax.domain.model.MediaType
import com.camerax.domain.model.ScanResult
import com.camerax.domain.model.TimerDelay
import com.camerax.domain.model.VideoQuality
import com.camerax.domain.repository.MediaRepository
import com.camerax.domain.repository.PreferencesRepository
import com.camerax.domain.repository.ScanHistoryRepository
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

class CameraViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val mediaRepository: MediaRepository,
    private val scanHistoryRepository: ScanHistoryRepository,
) : ViewModel() {
    companion object {
        private const val TAG = "CameraViewModel"
        private val FILENAME_FORMATTER = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
    }

    private val _cameraMode = MutableStateFlow(CameraMode.PHOTO)
    val cameraMode: StateFlow<CameraMode> = _cameraMode.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingDurationMs = MutableStateFlow(0L)
    val recordingDurationMs: StateFlow<Long> = _recordingDurationMs.asStateFlow()

    private val _zoomRatio = MutableStateFlow(1f)
    val zoomRatio: StateFlow<Float> = _zoomRatio.asStateFlow()

    private val _minZoom = MutableStateFlow(1f)
    val minZoom: StateFlow<Float> = _minZoom.asStateFlow()

    private val _maxZoom = MutableStateFlow(1f)
    val maxZoom: StateFlow<Float> = _maxZoom.asStateFlow()

    private val _showZoomSlider = MutableStateFlow(false)
    val showZoomSlider: StateFlow<Boolean> = _showZoomSlider.asStateFlow()

    private val _isFrontCamera = MutableStateFlow(false)
    val isFrontCamera: StateFlow<Boolean> = _isFrontCamera.asStateFlow()

    private val _flipRotation = MutableStateFlow(0f)
    val flipRotation: StateFlow<Float> = _flipRotation.asStateFlow()

    private val _countdownValue = MutableStateFlow(0)
    val countdownValue: StateFlow<Int> = _countdownValue.asStateFlow()

    private val _isCountingDown = MutableStateFlow(false)
    val isCountingDown: StateFlow<Boolean> = _isCountingDown.asStateFlow()

    private val _lastCapturedUri = MutableStateFlow<String?>(null)
    val lastCapturedUri: StateFlow<String?> = _lastCapturedUri.asStateFlow()

    private val _scanResult = MutableStateFlow<ScanResult?>(null)
    val scanResult: StateFlow<ScanResult?> = _scanResult.asStateFlow()

    private val _captureFlash = MutableStateFlow(false)
    val captureFlash: StateFlow<Boolean> = _captureFlash.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val flashMode =
        preferencesRepository.flashMode.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            FlashMode.OFF,
        )
    val timerDelay =
        preferencesRepository.timerDelay.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            TimerDelay.OFF,
        )
    val gridEnabled =
        preferencesRepository.gridEnabled.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false,
        )
    val hdrEnabled =
        preferencesRepository.hdrEnabled.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false,
        )
    val videoQuality =
        preferencesRepository.videoQuality.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            VideoQuality.FHD_1080,
        )

    private var camera: Camera? = null
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var activeRecording: Recording? = null
    private var previewView: PreviewView? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var recordingTimerJob: Job? = null
    private var zoomHideJob: Job? = null
    private var countdownJob: Job? = null

    private val barcodeScanner =
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build(),
        )

    fun onPreviewViewReady(view: PreviewView) {
        previewView = view
    }

    fun startCamera(
        context: Context,
        lifecycleOwner: LifecycleOwner,
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val provider = cameraProviderFuture.get()
                cameraProvider = provider
                bindCameraUseCases(provider, lifecycleOwner)
            } catch (e: Exception) {
                Log.e(TAG, "Camera init failed", e)
                _errorMessage.value = e.localizedMessage
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun bindCameraUseCases(
        provider: ProcessCameraProvider,
        lifecycleOwner: LifecycleOwner,
    ) {
        val pView = previewView ?: return

        provider.unbindAll()

        val cameraSelector =
            if (_isFrontCamera.value) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }

        val preview =
            Preview.Builder().build().also {
                it.setSurfaceProvider(pView.surfaceProvider)
            }

        val useCases = mutableListOf<androidx.camera.core.UseCase>(preview)

        when (_cameraMode.value) {
            CameraMode.PHOTO -> {
                imageCapture =
                    ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .setFlashMode(
                            when (flashMode.value) {
                                FlashMode.AUTO -> ImageCapture.FLASH_MODE_AUTO
                                FlashMode.ON -> ImageCapture.FLASH_MODE_ON
                                FlashMode.OFF, FlashMode.TORCH -> ImageCapture.FLASH_MODE_OFF
                            },
                        )
                        .build()
                useCases.add(imageCapture!!)
            }

            CameraMode.VIDEO -> {
                val quality =
                    when (videoQuality.value) {
                        VideoQuality.FHD_1080 -> Quality.FHD
                        VideoQuality.HD_720 -> Quality.HD
                        VideoQuality.SD_480 -> Quality.SD
                    }
                val recorder =
                    Recorder.Builder()
                        .setQualitySelector(QualitySelector.from(quality))
                        .build()
                videoCapture = VideoCapture.withOutput(recorder)
                useCases.add(videoCapture!!)
            }

            CameraMode.QR_SCANNER -> {
                imageAnalysis =
                    ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also { analysis ->
                            analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
                                val mediaImage = imageProxy.image
                                if (mediaImage != null) {
                                    val image =
                                        InputImage.fromMediaImage(
                                            mediaImage, imageProxy.imageInfo.rotationDegrees,
                                        )
                                    barcodeScanner.process(image)
                                        .addOnSuccessListener { barcodes ->
                                            val barcode = barcodes.firstOrNull()
                                            if (barcode != null && _scanResult.value == null) {
                                                val result =
                                                    ScanResult(
                                                        rawValue = barcode.rawValue.orEmpty(),
                                                        format = BarcodeMapper.mapFormat(barcode.format),
                                                        contentType = BarcodeMapper.mapType(barcode.valueType),
                                                        displayValue = barcode.displayValue.orEmpty(),
                                                        timestampMs = System.currentTimeMillis(),
                                                    )
                                                _scanResult.value = result
                                                viewModelScope.launch {
                                                    scanHistoryRepository.insertScan(result)
                                                }
                                            }
                                        }
                                        .addOnCompleteListener {
                                            imageProxy.close()
                                        }
                                } else {
                                    imageProxy.close()
                                }
                            }
                        }
                useCases.add(imageAnalysis!!)
            }
        }

        try {
            camera =
                provider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    *useCases.toTypedArray(),
                )

            camera?.let { cam ->
                _minZoom.value = cam.cameraInfo.zoomState.value?.minZoomRatio ?: 1f
                _maxZoom.value = cam.cameraInfo.zoomState.value?.maxZoomRatio ?: 1f
                _zoomRatio.value = cam.cameraInfo.zoomState.value?.zoomRatio ?: 1f

                if (flashMode.value == FlashMode.TORCH) {
                    cam.cameraControl.enableTorch(true)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Use case binding failed", e)
            _errorMessage.value = e.localizedMessage
        }
    }

    fun setCameraMode(
        mode: CameraMode,
        context: Context,
        lifecycleOwner: LifecycleOwner,
    ) {
        if (_isRecording.value) return
        _cameraMode.value = mode
        _scanResult.value = null
        startCamera(context, lifecycleOwner)
    }

    fun capturePhoto(context: Context) {
        val capture = imageCapture ?: return

        val timer = timerDelay.value
        if (timer != TimerDelay.OFF && !_isCountingDown.value) {
            startCountdown(timer.seconds) { capturePhotoInternal(context, capture) }
            return
        }
        if (!_isCountingDown.value) {
            capturePhotoInternal(context, capture)
        }
    }

    private fun startCountdown(
        seconds: Int,
        onComplete: () -> Unit,
    ) {
        countdownJob?.cancel()
        _isCountingDown.value = true
        countdownJob =
            viewModelScope.launch {
                for (i in seconds downTo 1) {
                    _countdownValue.value = i
                    delay(1000)
                }
                _countdownValue.value = 0
                _isCountingDown.value = false
                onComplete()
            }
    }

    @Suppress("MissingPermission")
    private fun capturePhotoInternal(
        context: Context,
        capture: ImageCapture,
    ) {
        val timestamp = FILENAME_FORMATTER.format(Date())
        val fileName = "CameraX_$timestamp.jpg"

        val contentValues =
            ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX")
                }
            }

        val outputOptions =
            ImageCapture.OutputFileOptions.Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues,
            ).build()

        _captureFlash.value = true
        viewModelScope.launch {
            delay(150)
            _captureFlash.value = false
        }

        capture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri?.toString().orEmpty()
                    _lastCapturedUri.value = savedUri
                    viewModelScope.launch {
                        mediaRepository.insertMedia(
                            MediaItem(
                                uri = savedUri,
                                type = MediaType.PHOTO,
                                timestampMs = System.currentTimeMillis(),
                                name = fileName,
                            ),
                        )
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed", exception)
                    _errorMessage.value = exception.localizedMessage
                }
            },
        )
    }

    @Suppress("MissingPermission")
    fun toggleVideoRecording(context: Context) {
        if (_isRecording.value) {
            stopRecording()
            return
        }

        val capture = videoCapture ?: return
        val timestamp = FILENAME_FORMATTER.format(Date())
        val fileName = "CameraX_$timestamp.mp4"

        val movieDir =
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
                "CameraX",
            )
        if (!movieDir.exists()) movieDir.mkdirs()
        val videoFile = File(movieDir, fileName)

        val outputOptions = FileOutputOptions.Builder(videoFile).build()

        activeRecording =
            capture.output
                .prepareRecording(context, outputOptions)
                .apply {
                    if (ContextCompat.checkSelfPermission(
                            context, Manifest.permission.RECORD_AUDIO,
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    ) {
                        withAudioEnabled()
                    }
                }
                .start(ContextCompat.getMainExecutor(context)) { event ->
                    when (event) {
                        is VideoRecordEvent.Start -> {
                            _isRecording.value = true
                            startRecordingTimer()
                        }
                        is VideoRecordEvent.Finalize -> {
                            _isRecording.value = false
                            stopRecordingTimer()
                            if (!event.hasError()) {
                                val uri = event.outputResults.outputUri.toString()
                                _lastCapturedUri.value = uri
                                MediaScannerConnection.scanFile(
                                    context,
                                    arrayOf(videoFile.absolutePath),
                                    arrayOf("video/mp4"),
                                    null,
                                )
                                viewModelScope.launch {
                                    mediaRepository.insertMedia(
                                        MediaItem(
                                            uri = uri,
                                            type = MediaType.VIDEO,
                                            timestampMs = System.currentTimeMillis(),
                                            name = fileName,
                                        ),
                                    )
                                }
                            } else {
                                Log.e(TAG, "Video recording error: ${event.error}")
                                _errorMessage.value = "Recording failed"
                            }
                        }
                    }
                }
    }

    private fun stopRecording() {
        activeRecording?.stop()
        activeRecording = null
    }

    private fun startRecordingTimer() {
        _recordingDurationMs.value = 0
        recordingTimerJob =
            viewModelScope.launch {
                while (true) {
                    delay(100)
                    _recordingDurationMs.value += 100
                }
            }
    }

    private fun stopRecordingTimer() {
        recordingTimerJob?.cancel()
        _recordingDurationMs.value = 0
    }

    fun onCaptureAction(context: Context) {
        when (_cameraMode.value) {
            CameraMode.PHOTO -> capturePhoto(context)
            CameraMode.VIDEO -> toggleVideoRecording(context)
            CameraMode.QR_SCANNER -> { /* Auto-scanning, no action needed */ }
        }
    }

    fun setZoomRatio(ratio: Float) {
        val clamped = ratio.coerceIn(_minZoom.value, _maxZoom.value)
        _zoomRatio.value = clamped
        camera?.cameraControl?.setZoomRatio(clamped)
        showZoomSliderTemporarily()
    }

    fun onPinchZoom(scaleFactor: Float) {
        val newZoom = (_zoomRatio.value * scaleFactor).coerceIn(_minZoom.value, _maxZoom.value)
        setZoomRatio(newZoom)
    }

    private fun showZoomSliderTemporarily() {
        _showZoomSlider.value = true
        zoomHideJob?.cancel()
        zoomHideJob =
            viewModelScope.launch {
                delay(2500)
                _showZoomSlider.value = false
            }
    }

    fun onTapToFocus(
        x: Float,
        y: Float,
    ) {
        val cam = camera ?: return
        val factory = SurfaceOrientedMeteringPointFactory(1f, 1f)
        val point = factory.createPoint(x, y)
        val action =
            FocusMeteringAction.Builder(point)
                .setAutoCancelDuration(3, java.util.concurrent.TimeUnit.SECONDS)
                .build()
        cam.cameraControl.startFocusAndMetering(action)
    }

    fun flipCamera(
        context: Context,
        lifecycleOwner: LifecycleOwner,
    ) {
        if (_isRecording.value) return
        _isFrontCamera.value = !_isFrontCamera.value
        _flipRotation.value += 180f
        startCamera(context, lifecycleOwner)
    }

    fun toggleFlash() {
        viewModelScope.launch {
            val nextMode =
                when (flashMode.value) {
                    FlashMode.OFF -> FlashMode.AUTO
                    FlashMode.AUTO -> FlashMode.ON
                    FlashMode.ON -> FlashMode.TORCH
                    FlashMode.TORCH -> FlashMode.OFF
                }
            preferencesRepository.setFlashMode(nextMode)

            val hasFlash = camera?.cameraInfo?.hasFlashUnit() ?: false
            if (hasFlash) {
                camera?.cameraControl?.enableTorch(nextMode == FlashMode.TORCH)
            }

            imageCapture?.flashMode =
                when (nextMode) {
                    FlashMode.AUTO -> ImageCapture.FLASH_MODE_AUTO
                    FlashMode.ON -> ImageCapture.FLASH_MODE_ON
                    FlashMode.OFF, FlashMode.TORCH -> ImageCapture.FLASH_MODE_OFF
                }
        }
    }

    fun toggleTimer() {
        viewModelScope.launch {
            val nextDelay =
                when (timerDelay.value) {
                    TimerDelay.OFF -> TimerDelay.THREE
                    TimerDelay.THREE -> TimerDelay.FIVE
                    TimerDelay.FIVE -> TimerDelay.TEN
                    TimerDelay.TEN -> TimerDelay.OFF
                }
            preferencesRepository.setTimerDelay(nextDelay)
        }
    }

    fun toggleGrid() {
        viewModelScope.launch {
            preferencesRepository.setGridEnabled(!gridEnabled.value)
        }
    }

    fun toggleHdr() {
        viewModelScope.launch {
            preferencesRepository.setHdrEnabled(!hdrEnabled.value)
        }
    }

    fun clearScanResult() {
        _scanResult.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        cameraExecutor.shutdown()
        barcodeScanner.close()
        countdownJob?.cancel()
        recordingTimerJob?.cancel()
        zoomHideJob?.cancel()
    }
}
