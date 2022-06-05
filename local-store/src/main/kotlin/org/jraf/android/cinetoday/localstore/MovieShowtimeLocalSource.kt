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
import org.jraf.android.cinetoday.localstore.theater.SelectByMovieId
import java.util.Date
import javax.inject.Inject

interface MovieShowtimeLocalSource {
    suspend fun addMovieShowtimes(localMovieShowtimes: List<LocalMovieShowtime>)
    fun getMovieShowtimes(movieId: String): Flow<List<LocalMovieShowtime>>
    fun getMovieList(): Flow<List<LocalMovie>>

}

class MovieShowtimeLocalSourceImpl @Inject constructor(
    private val database: Database,
) : MovieShowtimeLocalSource {
    override suspend fun addMovieShowtimes(localMovieShowtimes: List<LocalMovieShowtime>) {
        withContext(Dispatchers.IO) {
            database.transaction {
                for (localMovieShowtime in localMovieShowtimes) {
                    database.movieQueries.insert(
                        id = localMovieShowtime.movie.id,
                        title = localMovieShowtime.movie.title,
                        posterUrl = localMovieShowtime.movie.posterUrl
                    )

                    for (theaterIdToShowtime in localMovieShowtime.showtimes) {
                        val showtime = theaterIdToShowtime.value
                        database.showtimeQueries.insert(
                            id = showtime.id,
                            startsAt = showtime.startsAt.time,
                            projection = showtime.projection.joinToString(","),
                            languageVersion = showtime.languageVersion,
                        )

                        database.movieShowtimeQueries.insert(
                            movieId = localMovieShowtime.movie.id,
                            showtimeId = showtime.id,
                            theaterId = theaterIdToShowtime.key,
                        )
                    }
                }
            }
        }
    }

    override fun getMovieShowtimes(movieId: String): Flow<List<LocalMovieShowtime>> {
        return database.movieShowtimeQueries.selectByMovieId(id = movieId)
            .asFlow()
            .mapToList()
            .map { selectByMovieList ->
                val showTimesByMovieId: Map<String, List<SelectByMovieId>> = selectByMovieList.groupBy { it.movieId }
                showTimesByMovieId.map { (movieId, selectByMovieList) ->
                    val showtimes: Map<String, LocalShowtime> = selectByMovieList.associate { selectByMovie ->
                        selectByMovie.theaterId to LocalShowtime(
                            id = selectByMovie.showtimeId,
                            startsAt = Date(selectByMovie.startsAt),
                            projection = selectByMovie.projection.split(","),
                            languageVersion = selectByMovie.languageVersion,
                        )
                    }
                    LocalMovieShowtime(
                        movie = LocalMovie(
                            id = movieId,
                            title = selectByMovieList.first().title,
                            posterUrl = selectByMovieList.first().posterUrl
                        ),
                        showtimes = showtimes
                    )
                }
            }
    }

    override fun getMovieList(): Flow<List<LocalMovie>> {
        return database.movieQueries.selectAllMovies().asFlow().mapToList().map { movieList ->
            movieList.map { movie ->
                LocalMovie(
                    id = movie.id,
                    title = movie.title,
                    posterUrl = movie.posterUrl
                )
            }
        }
    }
}

data class LocalMovieShowtime(
    val movie: LocalMovie,
    val showtimes: Map<String, LocalShowtime>,
)

data class LocalMovie(
    val id: String,
    val title: String,
    val posterUrl: String?,
)

data class LocalShowtime(
    val id: String,
    val startsAt: Date,
    val projection: List<String>,
    val languageVersion: String?,
)
