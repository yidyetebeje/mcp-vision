package com.yidye.mcp_vision.analyzer

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ObjectDetectionAnalyzer(
    private val onObjectsDetected: (objects: List<DetectedObject>, width: Int, height: Int) -> Unit
) : ImageAnalysis.Analyzer {
    private val localModel = LocalModel.Builder()
        .setAssetFilePath("efficientdet_lite3.tflite")
        .build()
    private val options = CustomObjectDetectorOptions.Builder(localModel)
        .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
        .enableClassification()
        .setClassificationConfidenceThreshold(0.3f)
        .setMaxPerObjectLabelCount(2)
        .enableMultipleObjects()
        .build()
    private val detector = ObjectDetection.getClient(options)
    private val scope = CoroutineScope(Dispatchers.Default)
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        scope.launch{
            val mediaImage = imageProxy.image ?: run {
                imageProxy.close()
                return@launch
            }
            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            suspendCoroutine {
                continuation -> detector.process(inputImage).addOnSuccessListener {
                    onObjectsDetected(it, inputImage.width, inputImage.height)
                }.addOnCompleteListener {
                    continuation.resume(Unit)
                }
            }
            delay(100)

        }.invokeOnCompletion {
            imageProxy.close()
        }
    }
}