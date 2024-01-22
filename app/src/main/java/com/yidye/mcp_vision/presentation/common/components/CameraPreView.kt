package com.yidye.mcp_vision.presentation.common.components

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.Executors

//@Composable
//fun CameraView(
//    context: Context,
//    analyzer: ImageAnalysis.Analyzer,
//    lifecycleOwner: LifecycleOwner) {
//    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
//    var preview by remember { mutableStateOf<Preview?>(null) }
//    val executor = ContextCompat.getMainExecutor(context)
//    val cameraProvider = cameraProviderFuture.get()
//    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
//    var cameraSelector = remember {
//        CameraSelector.DEFAULT_BACK_CAMERA
//    }
//
//    // create a lambda function to toggle back and front camera
//    Column(
//         modifier = Modifier
//              .fillMaxWidth()
//              .fillMaxHeight()
//   ){
//       IconButton(onClick = {
//           // change camera
//           if(cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA)
//               cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//           else if(cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
//               cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
//
//       }, modifier = Modifier.padding(16.dp).size(500.dp)) {
//           Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "menu")
//       }
//       AndroidView(
//           modifier = Modifier
//               .fillMaxWidth()
//               .fillMaxHeight(),
//           factory = { ctx ->
//               val previewView = PreviewView(ctx)
//               cameraProviderFuture.addListener({
//                   val imageAnalysis = ImageAnalysis.Builder()
//                       .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                       .build()
//                       .apply {
//                           setAnalyzer(cameraExecutor, analyzer)
//                       }
//                   cameraProvider.unbindAll()
//                   cameraProvider.bindToLifecycle(
//                       lifecycleOwner,
//                       cameraSelector,
//                       imageAnalysis,
//                       preview
//                   )
//               }, executor)
//               preview = Preview.Builder().build().also {
//                   it.setSurfaceProvider(previewView.surfaceProvider)
//               }
//               previewView
//           }
//       )
//   }
//
//
//}


@Composable
fun CameraView(
    lifecycleOwner: LifecycleOwner,
    controller: LifecycleCameraController,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                PreviewView(it).apply {
                    this.controller = controller
                    controller.bindToLifecycle(lifecycleOwner)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}