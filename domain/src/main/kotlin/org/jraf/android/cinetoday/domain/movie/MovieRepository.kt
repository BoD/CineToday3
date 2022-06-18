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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jraf.android.cinetoday.api.MovieRemoteSource
import org.jraf.android.cinetoday.api.RemoteMovie
import org.jraf.android.cinetoday.api.RemoteShowtime
import org.jraf.android.cinetoday.localstore.LocalMovie
import org.jraf.android.cinetoday.localstore.LocalMovieShowtime
import org.jraf.android.cinetoday.localstore.LocalShowtime
import org.jraf.android.cinetoday.localstore.MovieShowtimeLocalSource
import org.jraf.android.cinetoday.util.datetime.isoDateStringToLocalDate
import java.time.LocalDate
import java.util.Date
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val movieRemoteSource: MovieRemoteSource,
    private val movieShowtimeLocalSource: MovieShowtimeLocalSource,
) {
    private suspend fun fetchMovies(theaterIds: Set<String>, from: Date, to: Date, coroutineScope: CoroutineScope): Map<String, List<RemoteMovie>> {
        return movieRemoteSource
            .fetchMovies(theaterIds = theaterIds, from = from, to = to, coroutineScope = coroutineScope)
    }

    private suspend fun saveMoviesWithShowtimes(movies: Map<String, List<RemoteMovie>>) {
        val localMovieShowtimes = mutableListOf<LocalMovieShowtime>()
        for ((theaterId, moviesForTheater) in movies) {
            for (movie in moviesForTheater) {
                val existingLocalMovieShowtime = localMovieShowtimes.find { it.movie.id == movie.id }
                val localMovieShowtime = LocalMovieShowtime(
                    movie = movie.toLocalMovie(),
                    showtimes = movie.showtimes.associate { theaterId to it.toLocalShowtime() } + (existingLocalMovieShowtime?.showtimes ?: emptyMap()),
                )
                if (existingLocalMovieShowtime != null) {
                    localMovieShowtimes.remove(existingLocalMovieShowtime)
                }
                localMovieShowtimes += localMovieShowtime
            }
        }
        movieShowtimeLocalSource.deleteAll()
        movieShowtimeLocalSource.addMovieShowtimes(localMovieShowtimes)
    }

    suspend fun fetchAndSaveMovies(theaterIds: Set<String>, from: Date, to: Date, coroutineScope: CoroutineScope) {
        val moviesByTheater: Map<String, List<RemoteMovie>> = fetchMovies(theaterIds, from, to, coroutineScope)
        saveMoviesWithShowtimes(moviesByTheater)
    }

    fun getMovieList(): Flow<List<Movie>> = movieShowtimeLocalSource.getMovieList().map { movieList -> movieList.map { localMovie -> localMovie.toMovie() } }

    fun getMovieWithShowtimes(id: String): Flow<Movie> =
        movieShowtimeLocalSource.getMovieWithShowtimes(id).map { localMovieShowtimes -> localMovieShowtimes.toMovie() }
}


private fun RemoteShowtime.toLocalShowtime() = LocalShowtime(
    id = id,
    theaterId = "", // Unused
    theaterName = "", // Unused
    startsAt = startsAt,
    projection = projection,
    languageVersion = languageVersion,
)

private fun RemoteMovie.toLocalMovie() = LocalMovie(
    id = id,
    title = title,
    posterUrl = posterUrl,
    releaseDate = isoDateStringToLocalDate(releaseDate),
    weeklyTheatersCount = weeklyTheatersCount,
    colorDark = colorDark,
    colorLight = colorLight,
)

private fun LocalMovie.toMovie() = Movie(
    id = id,
    title = title,
    posterUrl = posterUrl,
    releaseDate = releaseDate,
    colorDark = colorDark,
    colorLight = colorLight,
    showtimes = emptyList(),
)

private fun LocalMovieShowtime.toMovie() = Movie(
    id = movie.id,
    title = movie.title,
    posterUrl = movie.posterUrl,
    releaseDate = movie.releaseDate,
    colorDark = movie.colorDark,
    colorLight = movie.colorLight,
    showtimes = showtimes.map { it.value.toShowtime() }
)

private fun LocalShowtime.toShowtime() = Showtime(
    id = id,
    theaterId = theaterId,
    theaterName = theaterName,
    startsAt = startsAt,
    projection = projection,
    languageVersion = languageVersion,
)


data class Movie(
    val id: String,
    val title: String,
    val posterUrl: String?,
    val releaseDate: LocalDate,
    val colorDark: Int?,
    val colorLight: Int?,
    val showtimes: List<Showtime>,
)

data class Showtime(
    val id: String,
    val theaterId: String,
    val theaterName: String,
    val startsAt: Date,
    val projection: List<String>,
    val languageVersion: String?,
)
