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

import android.animation.ArgbEvaluator
import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import coil.compose.SubcomposeAsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import org.jraf.android.cinetoday.domain.movie.model.Movie
import org.jraf.android.cinetoday.domain.movie.model.fakeMovie
import org.jraf.android.cinetoday.ui.common.loading.Loading
import org.jraf.android.cinetoday.ui.movie.details.MovieDetailsActivity
import org.jraf.android.cinetoday.ui.theme.CineTodayColor
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
    val argbEvaluator = ArgbEvaluator()
    val pagerState = rememberPagerState()
    val backgroundColor: Color by derivedStateOf {
        val offset = pagerState.currentPageOffset
        val currentPage = pagerState.currentPage
        val movieA = if (offset >= 0) movieList[currentPage] else movieList[currentPage - 1]
        val movieB = if (offset > 0) movieList[currentPage + 1] else movieList[currentPage]

        val colorA = movieA.colorDark ?: CineTodayColor.MovieDefaultBackground.toArgb()
        val colorB = movieB.colorDark ?: CineTodayColor.MovieDefaultBackground.toArgb()

        val fraction = if (offset >= 0) offset else offset + 1
        val resultColor = argbEvaluator.evaluate(fraction, colorA, colorB) as Int
        Color(resultColor)
    }

    // Scale a bit more so the top and bottom white border don't appear
    // XXX: This only works because the Movie is full screen (using the screen's height)
    val borderPadding = 2.dp * 2
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val scaleFactor = screenHeight / (screenHeight - borderPadding)

    VerticalPager(
        count = movieList.size,
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) { page ->
        val movie = movieList[page]

        val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue
        val scale = lerp(
            start = 0.90f,
            stop = 1f,
            fraction = 1f - pageOffset.coerceIn(0f, 1f)
        ) * scaleFactor
        val rotation = lerp(
            start = -15F,
            stop = 0F,
            fraction = 1f - pageOffset.coerceIn(0f, 1f)
        )
        val alpha = lerp(
            start = .75F,
            stop = 0F,
            fraction = 1f - pageOffset.coerceIn(0f, 1f)
        )

        val context = LocalContext.current
        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    rotationX = rotation
                }
                .fillMaxWidth(fraction = .7F)
                .fillMaxHeight()
                .clickable {
                    context.startActivity(
                        Intent(context, MovieDetailsActivity::class.java)
                            .putExtra(MovieDetailsActivity.EXTRA_MOVIE_ID, movie.id)
                    )
                }
        ) {
            Movie(movie)

            // Make the movie darker by drawing a translucent black canvas on top of it
            Canvas(Modifier.fillMaxSize()) {
                drawRect(
                    color = Color(0f, 0f, 0f, alpha = alpha),
                    size = size,
                )
            }
        }
    }
}

@Composable
private fun Movie(movie: Movie) {
    SubcomposeAsyncImage(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(2.dp),
        model = movie.posterUrl,
        loading = { NoPosterMovie(movie) },
        error = { NoPosterMovie(movie) },
        contentScale = ContentScale.Crop,
        contentDescription = null,
    )
}

@Composable
private fun NoPosterMovie(movie: Movie) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = noPosterBrushPainter,
            contentDescription = movie.title
        )
        Text(
            modifier = Modifier.padding(4.dp),
            text = movie.title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.title2,
            maxLines = 6,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private val noPosterBrushPainter = BrushPainter(Brush.linearGradient(
    0.0f to Color.DarkGray,
    0.3f to CineTodayColor.MovieDefaultBackground,
    1.0f to Color.Black,
))


@Preview(device = Devices.WEAR_OS_LARGE_ROUND)
@Composable
private fun MoviePreview() {
    Movie(
        fakeMovie(),
    )
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND)
@Composable
private fun NoPosterMoviePreview() {
    NoPosterMovie(
        fakeMovie(),
    )
}


@Preview(device = Devices.WEAR_OS_LARGE_ROUND)
@Composable
private fun MovieListPreview() {
    MovieList(
        listOf(
            fakeMovie(),
            fakeMovie(),
        )
    )
}
