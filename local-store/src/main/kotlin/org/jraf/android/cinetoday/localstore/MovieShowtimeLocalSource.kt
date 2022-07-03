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
package org.jraf.android.cinetoday.localstore

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.jraf.android.cinetoday.localstore.theater.Movie
import org.jraf.android.cinetoday.localstore.theater.SelectByMovieId
import org.jraf.android.cinetoday.util.datetime.timestampToLocalDate
import org.jraf.android.cinetoday.util.datetime.toTimestamp
import java.time.LocalDate
import java.util.Date
import javax.inject.Inject

interface MovieShowtimeLocalSource {
    suspend fun deleteAll()
    suspend fun addMovieShowtimes(localMovieShowtimes: List<LocalMovieShowtime>)
    fun getMovieWithShowtimes(movieId: String): Flow<LocalMovieShowtime>
    fun getMovieList(): Flow<List<LocalMovie>>
}

class MovieShowtimeLocalSourceImpl @Inject constructor(
    private val database: Database,
) : MovieShowtimeLocalSource {
    override suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            database.transaction {
                database.theaterMovieShowtimeQueries.deleteAll()
                database.movieQueries.deleteAll()
            }
        }
    }

    override suspend fun addMovieShowtimes(localMovieShowtimes: List<LocalMovieShowtime>) {
        withContext(Dispatchers.IO) {
            database.transaction {
                for (localMovieShowtime in localMovieShowtimes) {
                    database.movieQueries.insert(
                        id = localMovieShowtime.movie.id,
                        title = localMovieShowtime.movie.title,
                        posterUrl = localMovieShowtime.movie.posterUrl,
                        releaseDate = localMovieShowtime.movie.releaseDate?.toTimestamp(),
                        weeklyTheatersCount = localMovieShowtime.movie.weeklyTheatersCount,
                        colorDark = localMovieShowtime.movie.colorDark?.toLong(),
                        colorLight = localMovieShowtime.movie.colorLight?.toLong(),
                        directors = localMovieShowtime.movie.directors,
                        genres = localMovieShowtime.movie.genres,
                        actors = localMovieShowtime.movie.actors,
                        synopsis = localMovieShowtime.movie.synopsis,
                        runtimeMinutes = localMovieShowtime.movie.runtimeMinutes,
                        originalTitle = localMovieShowtime.movie.originalTitle,
                    )

                    for (showtime in localMovieShowtime.showtimes) {
                        database.showtimeQueries.insert(
                            id = showtime.id,
                            startsAt = showtime.startsAt.time,
                            projection = showtime.projection.joinToString(","),
                            languageVersion = showtime.languageVersion,
                        )

                        database.theaterMovieShowtimeQueries.insert(
                            movieId = localMovieShowtime.movie.id,
                            showtimeId = showtime.id,
                            theaterId = showtime.theaterId,
                        )
                    }
                }
            }
        }
    }

    override fun getMovieWithShowtimes(movieId: String): Flow<LocalMovieShowtime> {
        return database.theaterMovieShowtimeQueries.selectByMovieId(id = movieId)
            .asFlow()
            .mapToList()
            .map { selectByMovieList ->
                val showTimesByMovieId: Map<String, List<SelectByMovieId>> = selectByMovieList.groupBy { it.movieId }
                showTimesByMovieId.map { (movieId, selectByMovieList) ->
                    val showtimes: List<LocalShowtime> = selectByMovieList.map { selectByMovie ->
                        LocalShowtime(
                            id = selectByMovie.showtimeId,
                            theaterId = selectByMovie.theaterId,
                            theaterName = selectByMovie.theaterName,
                            startsAt = Date(selectByMovie.startsAt),
                            projection = selectByMovie.projection.split(","),
                            languageVersion = selectByMovie.languageVersion,
                        )
                    }
                    val firstMovie = selectByMovieList.first()
                    LocalMovieShowtime(
                        movie = LocalMovie(
                            id = movieId,
                            title = firstMovie.title,
                            posterUrl = firstMovie.posterUrl,
                            releaseDate = firstMovie.releaseDate?.let { timestampToLocalDate(it) },
                            weeklyTheatersCount = firstMovie.weeklyTheatersCount,
                            colorDark = firstMovie.colorDark?.toInt(),
                            colorLight = firstMovie.colorLight?.toInt(),
                            directors = firstMovie.directors,
                            genres = firstMovie.genres,
                            actors = firstMovie.actors,
                            synopsis = firstMovie.synopsis,
                            runtimeMinutes = firstMovie.runtimeMinutes,
                            originalTitle = firstMovie.originalTitle,
                        ),
                        showtimes = showtimes
                    )
                }
                    .first()
            }

    }

    override fun getMovieList(): Flow<List<LocalMovie>> {
        return database.movieQueries.selectAllMovies().asFlow().mapToList().map { movieList ->
            movieList.map { sqlMovie ->
                sqlMovie.toLocalMovie()
            }
        }
    }
}

private fun Movie.toLocalMovie() = LocalMovie(
    id = id,
    title = title,
    posterUrl = posterUrl,
    releaseDate = releaseDate?.let { timestampToLocalDate(it) },
    weeklyTheatersCount = weeklyTheatersCount,
    colorDark = colorDark?.toInt(),
    colorLight = colorLight?.toInt(),
    directors = directors,
    genres = genres,
    actors = actors,
    synopsis = synopsis,
    runtimeMinutes = runtimeMinutes,
    originalTitle = originalTitle,
)

data class LocalMovieShowtime(
    val movie: LocalMovie,
    val showtimes: List<LocalShowtime>,
)

data class LocalMovie(
    val id: String,
    val title: String,
    val posterUrl: String?,
    val releaseDate: LocalDate?,
    val weeklyTheatersCount: Long,
    val colorDark: Int?,
    val colorLight: Int?,
    val directors: String,
    val genres: String,
    val actors: String,
    val synopsis: String?,
    val runtimeMinutes: Long,
    val originalTitle: String,
)

data class LocalShowtime(
    val id: String,
    val theaterId: String,
    val theaterName: String,
    val startsAt: Date,
    val projection: List<String>,
    val languageVersion: String?,
)
