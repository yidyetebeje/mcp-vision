package com.yidye.mcp_vision.presentation.screens.image_labeling

import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

import com.yidye.mcp_vision.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.label.ImageLabel
import com.yidye.mcp_vision.analyzer.ImageLabelingAnalyzer
import com.yidye.mcp_vision.presentation.common.components.CameraView
import com.yidye.mcp_vision.presentation.screens.object_detection.CameraOverlay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImageLabelingScreen(navController: NavController) {
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
    val imageLabels = remember { mutableStateListOf<ImageLabel>() }
    val analyzer = ImageLabelingAnalyzer {
        imageLabels.clear()
        imageLabels.addAll(it)
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
                    .height(150.dp),
            ) {
                Text(
                    text = imageLabels.joinToString(separator = "\n") { it.text },
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }
        }
    }
}