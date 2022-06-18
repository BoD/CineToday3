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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import dagger.hilt.android.AndroidEntryPoint
import org.jraf.android.cinetoday.domain.movie.Movie
import org.jraf.android.cinetoday.ui.theme.CineTodayTheme
import java.time.LocalDate

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
        Scaffold() {
            val movie: Movie? by viewModel.movie.collectAsState(null)
            if (movie == null) return@Scaffold
            MovieDetailsContent(movie!!)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MovieDetailsContent(movie: Movie) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(
            horizontal = LocalConfiguration.current.screenWidthDp.dp * (1 - WIDTH_FRACTION) / 2,
            vertical = LocalConfiguration.current.screenHeightDp.dp * .2F
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text(
                modifier = Modifier.background(Color.Red),
                text = movie.title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.title1
            )
        }

        item {
            Text(
                modifier = Modifier.background(Color.Green),
                text = "Ok",
                textAlign = TextAlign.Center
            )
        }

        stickyHeader {
            Text(
                modifier = Modifier.background(Color.Blue),
                text = "Foobar",
                textAlign = TextAlign.Center
            )

        }


        item {
            Text(
                modifier = Modifier.background(Color.Yellow),
                text = "Located off the coast of Costa Rica, the Jurassic World luxury resort provides a habitat for an array of genetically engineered dinosaurs, including the vicious and intelligent Indominus rex. When the massive creature escapes, it sets off a chain reaction that causes the other dinos to run amok. Now, it's up to a former military man and animal expert (Chris Pratt) to use his special skills to save two young brothers and the rest of the tourists from an all-out, prehistoric assault.",
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND)
@Composable
private fun MovieDetailsScreenPreview() {
    MovieDetailsScreen()
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND)
@Composable
private fun MovieDetailsContent() {
    MovieDetailsContent(Movie(
        id = "",
        title = "Jurassic Park",
        posterUrl = null,
        releaseDate = LocalDate.now(),
        colorDark = 0xFF000000.toInt(),
        colorLight = 0xFF000000.toInt(),
        showtimes = listOf()
    ))
}
