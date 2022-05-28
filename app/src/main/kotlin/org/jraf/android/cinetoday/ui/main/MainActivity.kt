/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2022-present Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jraf.android.cinetoday.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.PageIndicatorState
import androidx.wear.compose.material.Scaffold
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import org.jraf.android.cinetoday.repository.TheaterRepository
import org.jraf.android.cinetoday.ui.movie.list.MovieListScreen
import org.jraf.android.cinetoday.ui.settings.SettingsScreen
import org.jraf.android.cinetoday.ui.theater.list.TheaterListScreen
import org.jraf.android.cinetoday.ui.theme.CineTodayTheme
import org.jraf.android.cinetoday.util.logging.WEAR_PREVIEW_API_LEVEL
import org.jraf.android.cinetoday.util.logging.WEAR_PREVIEW_BACKGROUND_COLOR_BLACK
import org.jraf.android.cinetoday.util.logging.WEAR_PREVIEW_DEVICE_HEIGHT_DP
import org.jraf.android.cinetoday.util.logging.WEAR_PREVIEW_DEVICE_WIDTH_DP
import org.jraf.android.cinetoday.util.logging.WEAR_PREVIEW_SHOW_BACKGROUND
import org.jraf.android.cinetoday.util.logging.WEAR_PREVIEW_UI_MODE
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

private const val PAGE_COUNT = 3

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var theaterRepository: TheaterRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }

//        runBlocking {
//            try {
//                val theaters: List<Theater> = theaterRepository.search("nantes")
//                logd(theaters)
//                theaterRepository.addToFavorites(theaters[0])
//                val favorites: List<Theater> = theaterRepository.getFavorites().first()
//                logd("Favorites: $favorites")
////                theaterRepository.removeFromFavorites(favorites[0].id)
////                val favoritesAfter: List<Theater> = theaterRepository.getFavorites().first()
////                logd("Favorites after: $favoritesAfter")
//            } catch (e: Exception) {
//                logd(e)
//            }
//        }
    }
}

@Composable
private fun MainScreen() {
    CineTodayTheme {
        Scaffold {
            MainScreenContent()
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun MainScreenContent() {
    val pagerState: PagerState = rememberPagerState()
    LaunchedEffect(Unit) {
        // TODO
//        pagerState.scrollToPage(1)
    }
    HorizontalPager(
        count = PAGE_COUNT,
        modifier = Modifier.fillMaxSize(),
        state = pagerState,
    ) { page ->
        when (page) {
            0 -> TheaterListScreen()
            1 -> MovieListScreen()
            2 -> SettingsScreen()
        }
    }

    val pageIndicatorState = remember {
        object : PageIndicatorState {
            override val pageCount = PAGE_COUNT
            override val pageOffset get() = pagerState.currentPageOffset
            override val selectedPage get() = pagerState.currentPage
        }
    }
    val isScrollSettled = pagerState.currentPageOffset == 0F
    var isPagerIndicatorVisible by remember { mutableStateOf(true) }
    val animationOffsetPx: Int = with(LocalDensity.current) { 4.dp.roundToPx() }
    AnimatedVisibility(
        visible = isPagerIndicatorVisible,
        enter = slideInVertically(initialOffsetY = { animationOffsetPx }) + fadeIn(initialAlpha = 0.3f),
        exit = slideOutVertically(targetOffsetY = { animationOffsetPx }) + fadeOut()
    ) {
        HorizontalPageIndicator(pageIndicatorState = pageIndicatorState)
    }

    if (isScrollSettled) {
        LaunchedEffect(Unit) {
            delay(2.seconds)
            isPagerIndicatorVisible = false
        }
    } else {
        isPagerIndicatorVisible = true
    }
}

@Preview(
    widthDp = WEAR_PREVIEW_DEVICE_WIDTH_DP,
    heightDp = WEAR_PREVIEW_DEVICE_HEIGHT_DP,
    apiLevel = WEAR_PREVIEW_API_LEVEL,
    uiMode = WEAR_PREVIEW_UI_MODE,
    backgroundColor = WEAR_PREVIEW_BACKGROUND_COLOR_BLACK,
    showBackground = WEAR_PREVIEW_SHOW_BACKGROUND
)
@Composable
fun MainScreenPreview() {
    MainScreen()
}
