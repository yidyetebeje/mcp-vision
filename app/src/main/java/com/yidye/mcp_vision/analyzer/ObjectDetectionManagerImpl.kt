package com.yidye.mcp_vision.analyzer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.util.Log
import android.view.Surface


import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.vision.detector.ObjectDetector


/**
 * Manages the detection of objects within images using a TensorFlow Lite model
 */
class ObjectDetectionManagerImpl constructor(
    private val context: Context
) {
    // Holds the instance of the TensorFlow Lite object detector.
    private var detector: ObjectDetector? = null

    /**
     * Detects objects within the provided bitmap image.
     *
     * @param bitmap The input image in which objects are to be detected.
     * @param rotation The rotation value of the image to adjust its orientation.
     * @param confidenceThreshold The confidence score for filtering out results from model.
     * @return List of detected objects represented by the [Detection] class.
     */
    fun detectObjectsInCurrentFrame(
        bitmap: Bitmap,
        rotation: Int,
        confidenceThreshold: Float
    ): List<Detection> {
        if (detector == null) {
            initializeDetector(confidenceThreshold)
        }

        // Configure image processor for the given rotation.
        val imageProcessor =
            ImageProcessor.Builder()
                //.add(Rot90Op(-rotation / 90))     // Nullifying by adding rotation in CameraFrameAnalyzer class
                .build()

        // Convert the bitmap into a TensorImage for processing.
        val tensorImage: TensorImage = imageProcessor.process(
            TensorImage.fromBitmap(bitmap)
        )

        // Obtain Results
        val detectionResults = detector?.detect(
            tensorImage
        )

        // Map detected objects to 'Detection' and filter by confidence threshold
        return detectionResults?.mapNotNull { detectedObject ->
            if ((detectedObject.categories.firstOrNull()?.score ?: 0f) >= confidenceThreshold) {
                Detection(
                    boundingBox = detectedObject.boundingBox,
                    detectedObjectName = detectedObject.categories.firstOrNull()?.label ?: "",
                    confidenceScore = detectedObject.categories.firstOrNull()?.score ?: 0f,
                    tensorImage.height,
                    tensorImage.width
                )
            } else null
        }?.take(10) ?: emptyList()
    }

    /**
     * Initializes the TensorFlow Lite Object Detector with the given confidence threshold.
     *
     * @param confidenceThreshold The minimum confidence score required for a detected object to be considered.
     */
    private fun initializeDetector(confidenceThreshold: Float) {
        try {
            val baseOptions = BaseOptions.builder()
                .setNumThreads(2)

            // Using GPU if available
            if (CompatibilityList().isDelegateSupportedOnThisDevice) {
                baseOptions.useGpu()
            }

            val options = ObjectDetector.ObjectDetectorOptions.builder()
                .setBaseOptions(baseOptions.build())
                .setMaxResults(10)
                .setScoreThreshold(confidenceThreshold)
                .build()

            detector = ObjectDetector.createFromFileAndOptions(
                context,
                "efficientdet_lite3.tflite",
                options
            )
        } catch (exception: IllegalStateException) {
            exception.printStackTrace()
        }
    }

    /**
     * Builds and returns the TfLite ImageProcessingOptions
     *
     * @param rotation
     * @return ImageProcessingOptions
     */
    private fun initializeImageProcessingOptions(rotation: Int): ImageProcessingOptions {
        return ImageProcessingOptions.builder()
            .setOrientation(getOrientationFromRotation(rotation))
            .build()
    }

    /**
     * Returns the orientation of image from the rotation using the
     * TfLite ImageProcessingOptions.Orientation used to internally make use of image axes as an Image metadata
     *
     * @param rotation
     * @return ImageProcessingOptions.Orientation
     */
    private fun getOrientationFromRotation(rotation: Int): ImageProcessingOptions.Orientation {
        return when(rotation) {
            Surface.ROTATION_270 -> ImageProcessingOptions.Orientation.BOTTOM_RIGHT
            Surface.ROTATION_90 -> ImageProcessingOptions.Orientation.TOP_LEFT
            Surface.ROTATION_180 -> ImageProcessingOptions.Orientation.RIGHT_BOTTOM
            else -> ImageProcessingOptions.Orientation.RIGHT_TOP
        }
    }
}

data class Detection(
    val boundingBox: RectF,
    val detectedObjectName: String,
    val confidenceScore: Float,
    val tensorImageHeight: Int,
    val tensorImageWidth: Int,
)