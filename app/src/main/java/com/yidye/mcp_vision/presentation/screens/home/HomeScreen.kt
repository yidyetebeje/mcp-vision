package com.yidye.mcp_vision.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems


import com.yidye.mcp_vision.R
import com.yidye.mcp_vision.navigation.Screen
import com.yidye.mcp_vision.presentation.common.components.HomeMediaUI
import com.yidye.mcp_vision.presentation.common.components.ImageCard
import com.yidye.mcp_vision.presentation.common.components.MediaCarousel
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "menu")
                    }
                },
                title = { Text(text = stringResource(id = R.string.app_name)) })
        },
        contentColor = MaterialTheme.colorScheme.primary,
        content = {

            LazyColumn(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp),
                verticalArrangement =Arrangement.spacedBy(6.dp),
                contentPadding = it
            ) {
                item {
                    ImageCard(
                        title = stringResource(id = R.string.barcode_detection_title),
                        description = stringResource(id = R.string.barcode_detection_description),
                        imageUrl = stringResource(id = R.string.barcode_detection_url),
                        onCardClick = { navController.navigate(Screen.BarcodeScanning.route) }
                    )
                }

                item {
                    ImageCard(
                        title = stringResource(id = R.string.face_mesh_detection_title),
                        description = stringResource(id = R.string.face_mesh_detection_description),
                        imageUrl = stringResource(id = R.string.face_mesh_detection_url),
                        onCardClick = { navController.navigate(Screen.FaceMeshDetection.route) }
                    )
                }

                item {
                    ImageCard(
                        title = stringResource(id = R.string.text_recognition_title),
                        description = stringResource(id = R.string.text_recognition_description),
                        imageUrl = stringResource(id = R.string.text_recognition_url),
                        onCardClick = { navController.navigate(Screen.TextRecognition.route) }
                    )
                }

                item {
                    ImageCard(
                        title = stringResource(id = R.string.image_labeling_detection_title),
                        description = stringResource(id = R.string.image_labeling_detection_description),
                        imageUrl = stringResource(id = R.string.image_labeling_detection_url),
                        onCardClick = { navController.navigate(Screen.ImageLabeling.route) }
                    )
                }

                item {
                    ImageCard(
                        title = stringResource(id = R.string.object_detection_title),
                        description = stringResource(id = R.string.object_detection_description),
                        imageUrl = stringResource(id = R.string.object_detection_url),
                        onCardClick = { navController.navigate(Screen.ObjectDetection.route) }
                    )
                }
            }
        }
    )
}
@Preview
@Composable
fun preview(){
    HomeScreen(navController = NavController(LocalContext.current))
}