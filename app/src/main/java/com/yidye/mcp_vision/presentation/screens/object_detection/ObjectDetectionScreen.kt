package com.yidye.mcp_vision.presentation.screens.object_detection

import android.graphics.Paint
import android.graphics.PointF
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.yidye.mcp_vision.R
import kotlin.math.round

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.objects.DetectedObject
import com.yidye.mcp_vision.analyzer.CameraFrameAnalyzer
import com.yidye.mcp_vision.analyzer.Detection
import com.yidye.mcp_vision.analyzer.ObjectDetectionAnalyzer
import com.yidye.mcp_vision.analyzer.ObjectDetectionManagerImpl
import com.yidye.mcp_vision.presentation.common.components.CameraView
import com.yidye.mcp_vision.presentation.common.utils.adjustPoint
import com.yidye.mcp_vision.presentation.common.utils.adjustSize
import com.yidye.mcp_vision.presentation.common.utils.drawBounds
import kotlin.math.round

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ObjectDetectionScreen(navController: NavController) {
    val context = LocalContext.current
    val cameraPermissionState =
        rememberPermissionState(permission = android.Manifest.permission.CAMERA)

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
        }) {
        ScanSurface(navController = navController)
    }
}

@Composable
fun ScanSurface(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val detectedObjects = remember { mutableStateListOf<DetectedObject>() }
    var detections by remember {
        mutableStateOf(emptyList<Detection>())
    }
    val confidenceScoreState = remember {
        mutableStateOf(0.5f)
    }
    val cameraFrameAnalyzer =  remember {
        CameraFrameAnalyzer(
            objectDetectionManager = ObjectDetectionManagerImpl(
                context = context
            ),
            onObjectDetectionResults = {
                detections = it

                // Clear the previous RectFs and add all new ones

            },
            confidenceScoreState = confidenceScoreState
        )
    }
    val screenWidth = remember { mutableStateOf(context.resources.displayMetrics.widthPixels) }
    val screenHeight = remember { mutableStateOf(context.resources.displayMetrics.heightPixels) }

    val imageWidth = remember { mutableStateOf(480) }
    val imageHeight = remember { mutableStateOf(640) }
    val analyzer = ObjectDetectionAnalyzer { objects, width, height ->
        detectedObjects.clear()
        detectedObjects.addAll(objects)
        imageWidth.value = width
        imageHeight.value = height
    }
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_ANALYSIS or
                        CameraController.IMAGE_CAPTURE
            )
            setImageAnalysisAnalyzer(
                ContextCompat.getMainExecutor(context),
                cameraFrameAnalyzer
            )
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {

        CameraView(
            controller = remember {
                controller
            },
            lifecycleOwner = lifecycleOwner,
        )
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp).align(Alignment.BottomStart)

        ){


            val roundedConfidenceScore = String.format("%.2f", round(confidenceScoreState.value * 100) / 100)
            Text(text = "Confidence score: ${roundedConfidenceScore}", style = MaterialTheme.typography.labelMedium, color = Color.White)
            Slider(
                value = confidenceScoreState.value,
                onValueChange = {
                    confidenceScoreState.value = it
                },
                valueRange = 0f..1f,
                steps = 100,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxHeight()
        ) {
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


            CameraOverlay(detections = detections)
        }
    }
}



@Composable
fun DrawDetectedObjects(objects: List<DetectedObject>, imageWidth: Int, imageHeight: Int, screenWidth: Int, screenHeight: Int) {
    Text(text = "Detected objects: ${objects.size}", style = MaterialTheme.typography.headlineLarge, color = Color.White)
    val textMeasurer = rememberTextMeasurer()
    Canvas(modifier = Modifier.fillMaxSize()) {
        objects.forEach {
            val boundingBox = it.boundingBox.toComposeRect()
            val topLeft = adjustPoint(PointF(boundingBox.topLeft.x, boundingBox.topLeft.y), imageWidth, imageHeight, screenWidth, screenHeight)
            val size = adjustSize(boundingBox.size, imageWidth, imageHeight, screenWidth, screenHeight)
            drawBounds(topLeft, size, Color.Yellow, 10f)

            it.labels.forEach { label ->
                val text = "${label.text}: ${label.confidence}"
                drawText(
                    textMeasurer = textMeasurer,
                    text = text,
                    topLeft = Offset(topLeft.x, topLeft.y - 10.dp.toPx()),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                        background = Color.Black
                    )
                )
            }
        }

    }
}