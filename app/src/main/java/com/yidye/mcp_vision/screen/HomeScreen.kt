package com.yidye.mcp_vision.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberImagePainter
import com.yidye.mcp_vision.R
import com.yidye.mcp_vision.navigation.Screen
import com.yidye.mcp_vision.presentation.common.components.HomeMediaUI
import com.yidye.mcp_vision.presentation.common.components.MediaCarousel
import com.yidye.mcp_vision.ui.theme.MCPVisionTheme
import kotlinx.coroutines.flow.MutableStateFlow

class TileInfo {
    var title : String = ""
    var description : String = ""
    var imageUrl : String = ""
    var onClick : () -> Unit = {}
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController : NavController) {
    var tileInfoList = listOf(
        TileInfo().apply {
            title = stringResource(id = R.string.barcode_detection_title)
            description = stringResource(id = R.string.barcode_detection_description)
            imageUrl = stringResource(id = R.string.barcode_detection_url)
            onClick = { navController.navigate(Screen.BarcodeScanning.route) }
        },
        TileInfo().apply {
            title = stringResource(id = R.string.face_mesh_detection_title)
            description = stringResource(id = R.string.face_mesh_detection_description)
            imageUrl = stringResource(id = R.string.face_mesh_detection_url)
            onClick = { navController.navigate(Screen.FaceMeshDetection.route) }
        },
        TileInfo().apply {
            title = stringResource(id = R.string.image_labeling_detection_title)
            description = stringResource(id = R.string.image_labeling_detection_description)
            imageUrl = stringResource(id = R.string.image_labeling_detection_url)
            onClick = { navController.navigate(Screen.ImageLabeling.route) }
        },
        TileInfo().apply {
            title = stringResource(id = R.string.object_detection_title)
            description = stringResource(id = R.string.object_detection_description)
            imageUrl = stringResource(id = R.string.object_detection_url)
            onClick = { navController.navigate(Screen.ObjectDetection.route) }
        },

        TileInfo().apply {
            title = stringResource(id = R.string.text_recognition_title)
            description = stringResource(id = R.string.text_recognition_description)
            imageUrl = stringResource(id = R.string.text_recognition_url)
            onClick = { navController.navigate(Screen.TextRecognition.route) }
        },
    )
    Scaffold(
        topBar = { TopAppBar(title = {
            Text("MCP Vision")
        }) },
    ) {
        it -> Box(

            modifier = Modifier.padding(it)
        ) {
        val homeMediaList = listOf(
            HomeMediaUI(
                id = 1,
                name = "Face Mesh Detection",
                posterPath = "https://developers.google.com/static/ml-kit/vision/face-detection/images/face_detection2x.png",
                backdropPath = "",
                overview = "Detect face mesh info on close-range images."
            ),
            HomeMediaUI(
                id = 2,
                name = "Barcode Scanning",
                posterPath = "https://developers.google.com/static/ml-kit/vision/barcode-scanning/images/barcode_scanning2x.png",
                backdropPath = "",
                overview = "Scan and process barcodes. Supports most standard 1D and 2D formats."
            ),
            HomeMediaUI(
                id = 3,
                name = "Text Recognition",
                posterPath = "https://developers.google.com/static/ml-kit/vision/text-recognition/images/text_recognition2x.png",
                backdropPath = "",
                overview = "Recognize and extract text from images."
            ),
            HomeMediaUI(
                id = 4,
                name = "Image Labeling",
                posterPath = "https://developers.google.com/static/ml-kit/vision/image-labeling/images/image_labeling2x.png",
                backdropPath = "",
                overview = "Identify objects, locations, animal species, products, and more."
            ),
            HomeMediaUI(
                id = 5,
                name = "Object Detection",
                posterPath = "https://developers.google.com/static/ml-kit/vision/object-detection/images/object_detection2x.png",
                backdropPath = "",
                overview = "Localize and track in real-time one or more objects in the live camera feed."
            )
        )
        val homeMediaUIPagingItems = PagingData.from(homeMediaList)
        val homeMediaUIFlow = MutableStateFlow(homeMediaUIPagingItems)
        val lazyPagingItems = homeMediaUIFlow.collectAsLazyPagingItems()
        Column(
            modifier = Modifier.fillMaxSize(),
        ){
            MediaCarousel(list = lazyPagingItems , onItemClicked = {})
            Spacer(modifier = Modifier.size(10.dp))
            LazyColumn(){
                item {
                    Text("Vision Options", modifier = Modifier.padding(10.dp), style = MaterialTheme.typography.labelLarge)
                }
                items(tileInfoList.size){ index ->
                    VisionTile(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = tileInfoList[index].onClick,
                        tileInfo = tileInfoList[index]
                    )
                }
            }
        }

      }
    }
}
@Composable
fun VisionTile(modifier: Modifier = Modifier, onClick: () -> Unit, tileInfo: TileInfo){
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = rememberImagePainter(tileInfo.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Fit,
            )
            Column(Modifier.padding(8.dp)) {
                Text(
                    text = tileInfo.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = tileInfo.description,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
@Preview()
@Composable
fun preview(){
    MCPVisionTheme {

       HomeScreen(navController = NavController(LocalContext.current))
    }
}