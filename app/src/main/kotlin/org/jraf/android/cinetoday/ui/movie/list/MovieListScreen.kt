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
package org.jraf.android.cinetoday.ui.movie.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.util.lerp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import org.jraf.android.cinetoday.domain.movie.Movie
import org.jraf.android.cinetoday.ui.common.loading.Loading
import java.time.LocalDate
import kotlin.math.absoluteValue

@Composable
fun MovieListScreen(viewModel: MovieListViewModel = viewModel()) {
    val movieList by viewModel.movieList.collectAsState(emptyList())
    if (movieList.isEmpty()) {
        Loading(Modifier.fillMaxSize())
    } else {
        MovieList(movieList)
    }
}


@Composable
@OptIn(ExperimentalPagerApi::class)
private fun MovieList(movieList: List<Movie>) {
    VerticalPager(
        count = movieList.size,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val movie = movieList[page]
        Box(
            modifier = Modifier
                .graphicsLayer {
                    // Calculate the absolute offset for the current page from the
                    // scroll position. We use the absolute value which allows us to mirror
                    // any effects for both directions
                    val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                    // Animate scaleX and scaleY between 85% and 100%
                    val scale = lerp(
                        start = 0.85f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                    scaleX = scale
                    scaleY = scale

                    // We animate the alpha, between 50% and 100%
                    alpha = lerp(
                        start = 0.5f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                }
                .fillMaxWidth(fraction = .7F)
                .fillMaxHeight()
        ) {
            Movie(movie)
        }
    }
}

@Composable
private fun Movie(movie: Movie) {
    AsyncImage(
        modifier = Modifier
            .fillMaxSize(),
        model = ImageRequest.Builder(LocalContext.current)
            .data(movie.posterUrl)
            .crossfade(true)
            .build(),
        contentScale = ContentScale.Crop,
        contentDescription = null,
    )
}


@Preview(device = Devices.WEAR_OS_LARGE_ROUND)
@Composable
private fun MovieListPreview() {
    MovieList(
        listOf(
            Movie(
                id = "",
                title = "Titanic",
                posterUrl = null,
                releaseDate = LocalDate.now(),
            ),
            Movie(
                id = "",
                title = "Terminator 2",
                posterUrl = null,
                releaseDate = LocalDate.now(),
            ),
        )
    )
}
