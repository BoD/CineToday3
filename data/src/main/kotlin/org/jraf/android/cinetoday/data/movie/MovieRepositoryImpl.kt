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
package org.jraf.android.cinetoday.data.movie

import android.text.Html
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jraf.android.cinetoday.api.MovieRemoteDataSource
import org.jraf.android.cinetoday.api.RemoteMovie
import org.jraf.android.cinetoday.api.RemoteShowtime
import org.jraf.android.cinetoday.domain.movie.MovieRepository
import org.jraf.android.cinetoday.domain.movie.model.Movie
import org.jraf.android.cinetoday.domain.movie.model.Showtime
import org.jraf.android.cinetoday.localstore.LocalMovie
import org.jraf.android.cinetoday.localstore.LocalMovieShowtime
import org.jraf.android.cinetoday.localstore.LocalShowtime
import org.jraf.android.cinetoday.localstore.MovieShowtimeLocalDataSource
import org.jraf.android.cinetoday.util.datetime.isoDateStringToLocalDate
import java.util.Date
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val movieRemoteDataSource: MovieRemoteDataSource,
    private val movieShowtimeLocalDataSource: MovieShowtimeLocalDataSource,
) : MovieRepository {
    private suspend fun fetchMovies(theaterIds: Set<String>, from: Date, to: Date, coroutineScope: CoroutineScope): Map<String, List<RemoteMovie>> {
        return movieRemoteDataSource
            .fetchMovies(theaterIds = theaterIds, from = from, to = to, coroutineScope = coroutineScope)
    }

    private suspend fun saveMoviesWithShowtimes(movies: Map<String, List<RemoteMovie>>) {
        val localMovieShowtimes = mutableListOf<LocalMovieShowtime>()
        for ((theaterId, moviesForTheater) in movies) {
            for (movie in moviesForTheater) {
                val existingLocalMovieShowtime = localMovieShowtimes.find { it.movie.id == movie.id }
                val localMovieShowtime = LocalMovieShowtime(
                    movie = movie.toLocalMovie(),
                    showtimes = movie.showtimes.map { it.toLocalShowtime(theaterId) } + (existingLocalMovieShowtime?.showtimes ?: emptyList()),
                )
                if (existingLocalMovieShowtime != null) {
                    localMovieShowtimes.remove(existingLocalMovieShowtime)
                }
                localMovieShowtimes += localMovieShowtime
            }
        }
        movieShowtimeLocalDataSource.deleteAll()
        movieShowtimeLocalDataSource.addMovieShowtimes(localMovieShowtimes)
    }

    override suspend fun fetchAndSaveMovies(theaterIds: Set<String>, from: Date, to: Date, coroutineScope: CoroutineScope) {
        val moviesByTheater: Map<String, List<RemoteMovie>> = fetchMovies(theaterIds, from, to, coroutineScope)
        saveMoviesWithShowtimes(moviesByTheater)
    }

    override fun getMovieList(): Flow<List<Movie>> =
        movieShowtimeLocalDataSource.getMovieList().map { movieList -> movieList.map { localMovie -> localMovie.toMovie() } }

    override fun getMovieWithShowtimes(id: String): Flow<Movie> =
        movieShowtimeLocalDataSource.getMovieWithShowtimes(id).map { localMovieShowtimes -> localMovieShowtimes.toMovie() }
}


private fun RemoteShowtime.toLocalShowtime(theaterId: String) = LocalShowtime(
    id = id,
    theaterId = theaterId,
    theaterName = "", // Unused
    startsAt = startsAt,
    projection = projection,
    languageVersion = languageVersion,
)

private fun RemoteMovie.toLocalMovie() = LocalMovie(
    id = id,
    title = title,
    posterUrl = posterUrl,
    releaseDate = releaseDate?.let { isoDateStringToLocalDate(it) },
    weeklyTheatersCount = weeklyTheatersCount,
    colorDark = colorDark,
    colorLight = colorLight,
    directors = directors.joinToString(),
    genres = genres.joinToString(),
    actors = actors.joinToString(),
    synopsis = synopsis,
    runtimeMinutes = runtimeMinutes.toLong(),
    originalTitle = originalTitle,
)

private fun LocalMovie.toMovie() = Movie(
    id = id,
    title = title,
    posterUrl = posterUrl,
    releaseDate = releaseDate,
    colorDark = colorDark,
    colorLight = colorLight,
    directors = directors,
    showtimes = emptyList(),
    genres = genres,
    actors = actors,
    synopsis = synopsis,
    runtimeMinutes = runtimeMinutes.toInt(),
    originalTitle = originalTitle,
)

private fun LocalMovieShowtime.toMovie() = Movie(
    id = movie.id,
    title = movie.title,
    posterUrl = movie.posterUrl,
    releaseDate = movie.releaseDate,
    colorDark = movie.colorDark,
    colorLight = movie.colorLight,
    directors = movie.directors,
    showtimes = showtimes.map(LocalShowtime::toShowtime),
    genres = movie.genres,
    actors = movie.actors,
    synopsis = movie.synopsis?.let { Html.fromHtml(it, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH).toString() },
    runtimeMinutes = movie.runtimeMinutes.toInt(),
    originalTitle = movie.originalTitle
)

private fun LocalShowtime.toShowtime() = Showtime(
    id = id,
    theaterId = theaterId,
    theaterName = theaterName,
    startsAt = startsAt,
    projection = projection,
    languageVersion = languageVersion,
)
