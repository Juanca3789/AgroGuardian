package com.dev.jcctech.agroguardian.ui.screen.cropguardian

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.FlipCameraAndroid
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.FlashOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import java.nio.ByteBuffer

@OptIn(ExperimentalTime::class)
@Composable
fun CameraScreen(
    onFrameAnalyzed: (ByteArray) -> Unit,
    onCaptureClick: (Uri) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraPermission = Manifest.permission.CAMERA

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (!granted) {
                Toast.makeText(context, "Permiso de cámara requerido", Toast.LENGTH_LONG).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        launcher.launch(cameraPermission)
    }


    var flashEnabled by remember { mutableStateOf(false) }
    var useFrontCamera by remember { mutableStateOf(false) }
    var liveResult by remember { mutableStateOf("Analizando...") }

    val previewView = remember { PreviewView(context) }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
            .build()
    }

    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build().apply {
                setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                    val bytes = imageProxyToByteArray(imageProxy)
                    onFrameAnalyzed(bytes)
                    imageProxy.close()
                }
            }
    }

    LaunchedEffect(flashEnabled, useFrontCamera) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().apply {
            surfaceProvider = previewView.surfaceProvider
        }

        val selector = if (useFrontCamera) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }

        imageCapture.flashMode =
            if (flashEnabled) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            selector,
            preview,
            imageCapture,
            imageAnalysis
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        Text(
            text = liveResult,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
        )

        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 72.dp)
                .size(72.dp)
        ) {
            IconButton(onClick = {
                val file = File(
                    context.filesDir,
                    "captura_${Clock.System.now().toEpochMilliseconds()}.jpg"
                )
                val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

                imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            val savedUri = output.savedUri ?: Uri.fromFile(file)
                            onCaptureClick(savedUri)
                        }

                        override fun onError(exc: ImageCaptureException) {
                            // manejar error si quieres
                        }
                    }
                )
            }) {
                Icon(
                    imageVector = Icons.Outlined.PhotoCamera,
                    contentDescription = "Capturar",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        IconButton(
            onClick = { useFrontCamera = !useFrontCamera },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Icon(Icons.Outlined.FlipCameraAndroid, contentDescription = "Cambiar cámara")
        }

        IconButton(
            onClick = { flashEnabled = !flashEnabled },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = if (flashEnabled) Icons.Outlined.FlashOn else Icons.Outlined.FlashOff,
                contentDescription = "Flash"
            )
        }
    }
}
private fun imageProxyToByteArray(image: ImageProxy): ByteArray {
    val buffer: ByteBuffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return bytes
}