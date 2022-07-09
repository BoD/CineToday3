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
package org.jraf.android.cinetoday.ui.movie.details

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import dagger.hilt.android.AndroidEntryPoint
import org.jraf.android.cinetoday.R
import org.jraf.android.cinetoday.domain.movie.Movie
import org.jraf.android.cinetoday.domain.movie.Showtime
import org.jraf.android.cinetoday.domain.movie.fakeMovie
import org.jraf.android.cinetoday.ui.theme.CineTodayColor
import org.jraf.android.cinetoday.ui.theme.CineTodayTheme
import org.jraf.android.cinetoday.util.trig.paddingForWidthFraction

@AndroidEntryPoint
class MovieDetailsActivity : ComponentActivity() {
    companion object {
        const val EXTRA_MOVIE_ID = "EXTRAS_MOVIE_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieDetailsScreen()
        }
    }
}

private const val WIDTH_FRACTION = 0.8f

@Composable
private fun MovieDetailsScreen(viewModel: MovieDetailsViewModel = viewModel()) {
    CineTodayTheme {
        Scaffold {
            val movie: Movie? by viewModel.movie.collectAsState(null)
            if (movie == null) return@Scaffold
            val showtimesIn24HFormat by viewModel.showtimesIn24HFormat.collectAsState(false)
            MovieDetailsContent(movie!!, showtimesIn24HFormat)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MovieDetailsContent(movie: Movie, showtimesIn24HFormat: Boolean) {
    val (horizontalPadding, verticalPadding) = paddingForWidthFraction(
        width = LocalConfiguration.current.screenWidthDp,
        fraction = WIDTH_FRACTION
    )
    val backgroundColor = movie.colorDark?.let { Color(it) } ?: CineTodayColor.MovieDefaultBackground
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(
            horizontal = horizontalPadding.dp,
            vertical = verticalPadding.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = movie.title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.title1
            )
        }

        item {
            Text(
                text = stringResource(R.string.theater_details_directors, movie.directors),
                style = MaterialTheme.typography.caption1,
                textAlign = TextAlign.Center
            )
        }

        item {
            Text(
                text = movie.genres,
                style = MaterialTheme.typography.caption1,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
            )
        }

        if (movie.actors.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.theater_details_actors, movie.actors),
                    style = MaterialTheme.typography.caption1,
                    textAlign = TextAlign.Center
                )
            }
        }

        movie.synopsis?.let { synopsis ->
            item {
                Text(
                    text = synopsis,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Start,
                )
            }
        }

        for ((theaterName, showtimes) in movie.showtimesPerTheater) {
            stickyHeader {
                Text(
                    modifier = Modifier
                        .background(backgroundColor)
                        .fillMaxWidth()
                        .padding(top = verticalPadding.dp),
                    text = theaterName,
                    style = MaterialTheme.typography.title2,
                    textAlign = TextAlign.Center
                )

                // Fading edge
                Spacer(Modifier
                    .height(4.dp)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                backgroundColor,
                                Color.Transparent
                            )
                        )
                    )
                )
            }
            for (showtime in showtimes) {
                item {
                    Showtime(showtime, showtimesIn24HFormat)
                }
            }
        }
    }
}

@Composable
private fun Showtime(showtime: Showtime, showtimesIn24HFormat: Boolean) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .let {
                if (showtime.isTooLate()) {
                    it.alpha(0.5f)
                } else {
                    it
                }
            }

    ) {
        ShowtimeTime(showtime, showtimesIn24HFormat)
        if (showtime.isDubbed) {
            ShowtimeTag(stringResource(R.string.theater_details_tag_dubbed))
        }
        if (showtime.is3D) {
            ShowtimeTag(stringResource(R.string.theater_details_tag_3d))
        }
        if (showtime.isImax) {
            ShowtimeTag(stringResource(R.string.theater_details_tag_imax))
        }
    }
}

@Composable
private fun ShowtimeTime(showtime: Showtime, showtimesIn24HFormat: Boolean) {
    Text(
        text = showtime.startsAtFormatted(showtimesIn24HFormat),
        color = Color.Black,
        style = MaterialTheme.typography.body1,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .background(
                shape = RoundedCornerShape(corner = CornerSize(4.dp)),
                color = CineTodayColor.ShowtimeTimeBackground
            )
            .padding(horizontal = 4.dp, vertical = 2.dp)
    )
}

@Composable
private fun ShowtimeTag(tag: String) {
    Text(
        text = tag,
        color = CineTodayColor.ShowtimeTag,
        style = MaterialTheme.typography.caption2,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .border(width = 1.dp, color = CineTodayColor.ShowtimeTag, shape = RoundedCornerShape(corner = CornerSize(4.dp)))
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .fillMaxHeight()
            .wrapContentHeight()
    )
}


@Preview(device = Devices.WEAR_OS_LARGE_ROUND)
@Composable
private fun MovieDetailsScreenPreview() {
    MovieDetailsScreen()
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND)
@Composable
private fun MovieDetailsContent() {
    MovieDetailsContent(fakeMovie(), true)
}
