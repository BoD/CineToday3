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

import org.jraf.android.cinetoday.api.MovieRemoteSource
import org.jraf.android.cinetoday.api.RemoteMovie
import java.util.Date
import javax.inject.Inject

interface MovieRepository {
    suspend fun getMovies(theaterIds: Set<String>, from: Date, to: Date): List<Movie>
}

class MovieRepositoryImpl @Inject constructor(
    private val movieRemoteSource: MovieRemoteSource,
) : MovieRepository {
    override suspend fun getMovies(theaterIds: Set<String>, from: Date, to: Date): List<Movie> {
        return movieRemoteSource.getMovies(theaterIds = theaterIds, from = from, to = to).map { it.toMovie() }
    }
}

private fun RemoteMovie.toMovie() = Movie(id = id, title = title, posterUrl = posterUrl)

data class Movie(
    val id: String,
    val title: String,
    val posterUrl: String?,
)

