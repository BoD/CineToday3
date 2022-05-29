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
package org.jraf.android.cinetoday.domain.movie

import kotlinx.coroutines.CoroutineScope
import org.jraf.android.cinetoday.api.MovieRemoteSource
import org.jraf.android.cinetoday.api.RemoteMovie
import org.jraf.android.cinetoday.api.RemoteShowtime
import org.jraf.android.cinetoday.localstore.LocalMovie
import org.jraf.android.cinetoday.localstore.LocalMovieShowtime
import org.jraf.android.cinetoday.localstore.LocalShowtime
import org.jraf.android.cinetoday.localstore.MovieShowtimeLocalSource
import java.util.Date
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val movieRemoteSource: MovieRemoteSource,
    private val movieShowtimeLocalSource: MovieShowtimeLocalSource,
) {
    suspend fun fetchMovies(theaterIds: Set<String>, from: Date, to: Date, coroutineScope: CoroutineScope): Map<String, List<Movie>> {
        return movieRemoteSource
            .getMovies(theaterIds = theaterIds, from = from, to = to, coroutineScope = coroutineScope)
            .mapValues { entry -> entry.value.map { it.toMovie() } }
    }

    suspend fun saveMoviesWithShowtimes(movies: Map<String, List<Movie>>) {
        val localMovieShowtimes = mutableListOf<LocalMovieShowtime>()
        for ((theaterId, moviesForTheater) in movies) {
            for (movie in moviesForTheater) {
                val previousLocalMovieShowtime = localMovieShowtimes.find { it.movie.id == movie.id }
                val localMovieShowtime = LocalMovieShowtime(
                    movie = movie.toLocalMovie(),
                    showtimes = movie.showtimes.associate { theaterId to it.toLocalShowtime() } + (previousLocalMovieShowtime?.showtimes ?: emptyMap()),
                )
                if (previousLocalMovieShowtime != null) {
                    localMovieShowtimes.remove(previousLocalMovieShowtime)
                }
                localMovieShowtimes += localMovieShowtime
            }
        }
        movieShowtimeLocalSource.addMovieShowtimes(localMovieShowtimes)
    }
}

private fun Showtime.toLocalShowtime() = LocalShowtime(id = id, startsAt = startsAt, projection = projection, languageVersion = languageVersion)

private fun Movie.toLocalMovie() = LocalMovie(id = id, title = title, posterUrl = posterUrl)

private fun RemoteMovie.toMovie() = Movie(
    id = id,
    title = title,
    posterUrl = posterUrl,
    showtimes = showtimes.map { it.toShowtime() },
)

private fun RemoteShowtime.toShowtime() = Showtime(
    id = id,
    startsAt = startsAt,
    projection = projection,
    languageVersion = languageVersion,
)

data class Movie(
    val id: String,
    val title: String,
    val posterUrl: String?,

    val showtimes: List<Showtime>,
)

data class Showtime(
    val id: String,
    val startsAt: Date,
    val projection: List<String>,
    val languageVersion: String?,
)
