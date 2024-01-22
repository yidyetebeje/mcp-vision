package com.yidye.mcp_vision.presentation.screens.barcode_scanning

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.PointF
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController


import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.common.Barcode
import com.yidye.mcp_vision.R
import com.yidye.mcp_vision.analyzer.BarcodeScanningAnalyzer
import com.yidye.mcp_vision.presentation.common.components.CameraView
import com.yidye.mcp_vision.presentation.common.utils.adjustPoint
import com.yidye.mcp_vision.presentation.common.utils.adjustSize
import com.yidye.mcp_vision.presentation.common.utils.drawBounds

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BarcodeScanningScreen(navController: NavController) {
    val context = LocalContext.current
    val cameraPermissionState =
        rememberPermissionState(permission = Manifest.permission.CAMERA)

    PermissionRequired(
        permissionState = cameraPermissionState,
        permissionNotGrantedContent = {
            LaunchedEffect(Unit) {
                cameraPermissionState.launchPermissionRequest()
            }
        },
        permissionNotAvailableContent = {
            Column {
                Toast.makeText(context, "Permission denied.", Toast.LENGTH_LONG).show()
            }
        },
        content = {
            ScanSurface(navController = navController)
        }
    )
}

@Composable
fun ScanSurface(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val detectedBarcode = remember { mutableStateListOf<Barcode>() }

    val screenWidth = remember { mutableStateOf(context.resources.displayMetrics.widthPixels) }
    val screenHeight = remember { mutableStateOf(context.resources.displayMetrics.heightPixels) }

    val imageWidth = remember { mutableStateOf(0) }
    val imageHeight = remember { mutableStateOf(0) }
    val analyzer = BarcodeScanningAnalyzer { barcodes, width, height ->
        detectedBarcode.clear()
        detectedBarcode.addAll(barcodes)
        imageWidth.value = width
        imageHeight.value = height
    }
    val controller = LifecycleCameraController(context).apply {
        setEnabledUseCases(
            CameraController.IMAGE_ANALYSIS or
                    CameraController.IMAGE_CAPTURE
        )
        setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(context),
            analyzer
        )
    }
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val textToCopy = remember { mutableStateOf(detectedBarcode.joinToString(separator = "\n") { it.displayValue.toString() }) }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraView(
            controller = remember {
                controller
            },
            lifecycleOwner = lifecycleOwner,
        )

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxHeight()
        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier
//                    .fillMaxWidth()
//            ) {
//                IconButton(onClick = { navController.navigateUp() }) {
//                    Icon(
//                        imageVector = Icons.Filled.ArrowBack,
//                        contentDescription = "back",
//                        tint = Color.White
//                    )
//                }
//                Text(
//                    text = stringResource(id = R.string.barcode_detection_title),
//                    style = MaterialTheme.typography.titleLarge,
//                    color = Color.White
//                )
//            }
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ){
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },

                    ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Button(onClick = {
                    controller.cameraSelector = if(controller.cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA)
                        CameraSelector.DEFAULT_BACK_CAMERA
                    else
                        CameraSelector.DEFAULT_FRONT_CAMERA
                }, modifier = Modifier.padding(16.dp)) {
                    Text(text = "Switch Camera")
                }
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(vertical = 10.dp),
            ) {
                IconButton(
                    onClick = {
                        val clip = ClipData.newPlainText("barcode", textToCopy.value)
                        clipboardManager.setPrimaryClip(clip)
                    }
                ){
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Copy to clipboard"
                    )
                }
                Text(
                    text = detectedBarcode.joinToString(separator = "\n") { it.displayValue.toString() },
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
        DrawBarcode(
            barcodes = detectedBarcode,
            imageWidth = imageWidth.value,
            imageHeight = imageHeight.value,
            screenWidth = screenWidth.value,
            screenHeight = screenHeight.value
        )
    }
}

@Composable
fun DrawBarcode(barcodes : List<Barcode>, imageWidth: Int, imageHeight: Int, screenWidth: Int, screenHeight: Int) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        barcodes.forEach { barcode ->
            barcode.boundingBox?.toComposeRect()?.let {
                val topLeft = adjustPoint(PointF(it.topLeft.x, it.topLeft.y), imageWidth, imageHeight, screenWidth, screenHeight)
                val size = adjustSize(it.size, imageWidth, imageHeight, screenWidth, screenHeight)
                drawBounds(topLeft, size, Color.Yellow, 10f)
            }
        }
    }
}