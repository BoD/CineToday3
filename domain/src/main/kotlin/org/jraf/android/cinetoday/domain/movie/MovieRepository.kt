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

import android.text.Html
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
import org.jraf.android.cinetoday.util.datetime.formatHourMinute
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
                    showtimes = movie.showtimes.map { it.toLocalShowtime(theaterId) } + (existingLocalMovieShowtime?.showtimes ?: emptyList()),
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


data class Movie(
    val id: String,
    val title: String,
    val posterUrl: String?,
    val releaseDate: LocalDate?,
    val colorDark: Int?,
    val colorLight: Int?,
    val directors: String,
    val showtimes: List<Showtime>,
    val genres: String,
    val actors: String,
    val synopsis: String?,
    val runtimeMinutes: Int,
    val originalTitle: String,
) {
    val showtimesPerTheater: Map<String, List<Showtime>> by lazy { showtimes.groupBy { it.theaterName } }
}

data class Showtime(
    val id: String,
    val theaterId: String,
    val theaterName: String,
    val startsAt: Date,
    val projection: List<String>,
    val languageVersion: String?,
) {
    fun isTooLate(): Boolean {
        return Date().after(startsAt)
    }

    val startsAtFormatted: String by lazy { formatHourMinute(startsAt) }

    val is3D: Boolean by lazy { projection.any { it.contains("3d", ignoreCase = true) } }
    val isImax: Boolean by lazy { projection.any { it.contains("imax", ignoreCase = true) } }
    val isDubbed: Boolean by lazy { languageVersion == "DUBBED" }
}

fun fakeMovie() = Movie(
    id = "",
    title = "Jurassic Park",
    posterUrl = null,
    releaseDate = LocalDate.now(),
    colorDark = 0xFF000000.toInt(),
    colorLight = 0xFF000000.toInt(),
    showtimes = listOf(
        Showtime(
            id = "id",
            theaterId = "theaterId",
            theaterName = "UGC Gobelins",
            startsAt = Date(),
            projection = listOf(),
            languageVersion = "VF"
        ),
        Showtime(
            id = "id",
            theaterId = "theaterId",
            theaterName = "UGC Gobelins",
            startsAt = Date(),
            projection = listOf("3D"),
            languageVersion = "VF"
        ),
        Showtime(
            id = "id",
            theaterId = "theaterId",
            theaterName = "UGC Gobelins",
            startsAt = Date(),
            projection = listOf("3D", "Imax"),
            languageVersion = "VF"
        )


    ),
    directors = "Steven Spielberg",
    genres = "Thriller",
    actors = "Sam Neill, Laura Dern, Jeff Goldblum, Richard Attenborough",
    synopsis = "A wealthy entrepreneur is visiting his son's theme park, which is full of the undead. He spots a zombie and, unaware of its existence, attacks it. The zombie is killed, but the park is left in a state of disrepair. The park's director, John Hammond, has given the zombie a new name, and the zombie is now named Jurassic Park. The park is visited by a variety of visitors, including a beautiful, but eccentric, jurassic park ranger, Charles Bronson. The ranger is tasked with protecting the park from the dinosaurs, but the park's security is compromised when a dinosaur attack is thwarted by a young dinosaur named Colin, who is a trained hunter. The ranger must now protect the park from the growing number of dinosaurs, and the park's security is threatened by the arrival of a new dinosaur species. The park is plagued by a variety of dinosaurs, and the ranger must use his skills to save the park from these dinosaurs.",
    runtimeMinutes = 120,
    originalTitle = "Jurassic Park",
)
