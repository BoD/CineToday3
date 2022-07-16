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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.PageIndicatorState
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.SwipeToDismissBoxState
import androidx.wear.compose.material.SwipeToDismissValue
import androidx.wear.compose.material.edgeSwipeToDismiss
import androidx.wear.compose.material.rememberSwipeToDismissBoxState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import org.jraf.android.cinetoday.ui.common.loading.ClipIfRound
import org.jraf.android.cinetoday.ui.movie.list.MovieListScreen
import org.jraf.android.cinetoday.ui.settings.SettingsScreen
import org.jraf.android.cinetoday.ui.theater.list.TheaterListScreen
import org.jraf.android.cinetoday.ui.theme.CineTodayTheme
import kotlin.time.Duration.Companion.seconds

private const val PAGE_COUNT = 3

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(onDismiss = { ActivityCompat.finishAffinity(this) })
        }
    }
}

@Composable
private fun MainScreen(onDismiss: () -> Unit = {}) {
    CineTodayTheme {
        val swipeToDismissBoxState = rememberSwipeToDismissBoxState()
        SwipeToDismissBox(state = swipeToDismissBoxState) { bg ->
            if (!bg) MainScreenContent(swipeToDismissBoxState = swipeToDismissBoxState)
        }
        LaunchedEffect(swipeToDismissBoxState.currentValue) {
            if (swipeToDismissBoxState.currentValue == SwipeToDismissValue.Dismissed) {
                onDismiss()
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun MainScreenContent(swipeToDismissBoxState: SwipeToDismissBoxState) {
    val isRound = LocalConfiguration.current.isScreenRound

    val pagerState: PagerState = rememberPagerState()
    LaunchedEffect(Unit) {
        pagerState.scrollToPage(1)
    }
    HorizontalPager(
        count = PAGE_COUNT,
        modifier = Modifier
            .fillMaxSize()
            .edgeSwipeToDismiss(swipeToDismissBoxState),
        state = pagerState,
    ) { page ->
        when (page) {
            0 -> TheaterListScreen()
            1 -> ClipIfRound(isRound) { MovieListScreen() }
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
    val isScrollSettled by derivedStateOf { pagerState.currentPageOffset == 0F }
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


