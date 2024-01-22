package com.yidye.mcp_vision.navigation

import com.yidye.mcp_vision.presentation.screens.barcode_scanning.BarcodeScanningScreen
import com.yidye.mcp_vision.presentation.screens.face_mesh_detection.FaceMeshDetectionScreen

import com.yidye.mcp_vision.presentation.screens.image_labeling.ImageLabelingScreen
import com.yidye.mcp_vision.presentation.screens.object_detection.ObjectDetectionScreen
import com.yidye.mcp_vision.presentation.screens.text_recognition.TextRecognitionScreen



import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.yidye.mcp_vision.screen.HomeScreen


internal sealed class Screen(val route: String) {
    object Home : Screen("home")
    object FaceMeshDetection : Screen("face_mesh_detection")
    object TextRecognition : Screen("text_recognition")
    object ObjectDetection : Screen("object_detection")
    object BarcodeScanning : Screen("barcode_scanning")
    object ImageLabeling : Screen("image_labeling")

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(route = Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(route = Screen.TextRecognition.route) {
            TextRecognitionScreen(navController)
        }
        composable(route = Screen.ObjectDetection.route) {
            ObjectDetectionScreen(navController)
        }
        composable(route = Screen.FaceMeshDetection.route) {
            FaceMeshDetectionScreen(navController)
        }
        composable(route = Screen.BarcodeScanning.route) {
            BarcodeScanningScreen(navController)
        }
        composable(route = Screen.ImageLabeling.route) {
            ImageLabelingScreen(navController)
        }
    }
}
