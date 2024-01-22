package com.yidye.mcp_vision.presentation.common.components


import androidx.annotation.Keep
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.util.lerp
import androidx.paging.PagingData
import coil.compose.AsyncImage
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.yidye.mcp_vision.R

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class
)
@Composable
fun MediaCarousel(
    list: LazyPagingItems<HomeMediaUI>,
    totalItemsToShow: Int = 10,
    carouselLabel: String = "",
    autoScrollDuration: Long = Constants.CAROUSEL_AUTO_SCROLL_TIMER,
    onItemClicked: (HomeMediaUI) -> Unit
) {
    val pageCount = list.itemCount.coerceAtMost(totalItemsToShow)
    val pagerState: PagerState = rememberPagerState(pageCount = { pageCount })
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()
    if (isDragged.not()) {
        with(pagerState) {
            if (pageCount > 0) {
                var currentPageKey by remember { mutableIntStateOf(0) }
                LaunchedEffect(key1 = currentPageKey) {
                    launch {
                        delay(timeMillis = autoScrollDuration)
                        val nextPage = (currentPage + 1).mod(pageCount)
                        animateScrollToPage(
                            page = nextPage,
                            animationSpec = tween(
                                durationMillis = Constants.ANIM_TIME_LONG
                            )
                        )
                        currentPageKey = nextPage
                    }
                }
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box {
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(
                    horizontal = dimensionResource(id = R.dimen.double_padding)
                ),
                pageSpacing = dimensionResource(id = R.dimen.normal_padding)
            ) { page: Int ->
                val item: HomeMediaUI? = list[page]
                item?.let {
                    Card(
                        onClick = { onItemClicked(it) },
                        modifier = Modifier.carouselTransition(
                            page,
                            pagerState
                        )
                    ) {
                        CarouselBox(it)
                    }
                }
            }
        }

        if (carouselLabel.isNotBlank()) {
            Text(
                text = carouselLabel,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun CarouselBox(item: HomeMediaUI) {
    Box {
        AsyncImage(
            model = item.posterPath,
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.ic_launcher_background),
            error = painterResource(id = R.drawable.ic_launcher_background),
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .height(
                    dimensionResource(id = R.dimen.home_grid_poster_height)
                )
                .fillMaxWidth()
        )
        val gradient = remember {
            Brush.verticalGradient(
                listOf(
                    Color.Transparent,
                    Color(0xE6000000)
                )
            )
        }

        Text(
            text = item.name,
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(gradient)
                .padding(
                    horizontal = dimensionResource(id = R.dimen.normal_padding),
                    vertical = dimensionResource(id = R.dimen.small_padding)
                )
        )
    }
}

@Keep
data class HomeMediaUI(
    val id: Int,
    val name: String,
    val posterPath: String,
    val backdropPath: String,
    val overview: String
)

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.carouselTransition(
    page: Int,
    pagerState: PagerState
) = graphicsLayer {
    val pageOffset =
        ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue

    val transformation = lerp(
        start = 0.8f,
        stop = 1f,
        fraction = 1f - pageOffset.coerceIn(
            0f,
            1f
        )
    )
    alpha = transformation
    scaleY = transformation
}
object Constants {
    const val NONE: String = "none"
    const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
    const val TMDB_IMAGE_URL = "https://image.tmdb.org/t/p/w780/"
    const val YOUTUBE_THUMB_URL = "https://img.youtube.com/vi/"
    const val ITEM_LOAD_PER_PAGE: Int = 10
    const val MEDIA_TYPE_MOVIE = "movie"
    const val MEDIA_TYPE_TV_SHOW = "tv"
    const val CAROUSEL_AUTO_SCROLL_TIMER: Long = 3000L
    const val VIDEO_TYPE_TRAILER: String = "Trailer"
    const val ANIM_TIME_SHORT: Int = 300
    const val ANIM_TIME_MEDIUM: Int = 500
    const val ANIM_TIME_LONG: Int = 800
}

@Preview
@Composable
fun carouselPreview() {
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
    MediaCarousel(list = lazyPagingItems , onItemClicked = {})
}