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
package org.jraf.android.cinetoday.api

import com.apollographql.apollo3.ApolloClient
import java.util.Date
import javax.inject.Inject

interface MovieRemoteSource {
    suspend fun getMovies(theaterIds: Set<String>, from: Date, to: Date): List<RemoteMovie>
}

class MovieRemoteSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient,
) : MovieRemoteSource {
    override suspend fun getMovies(theaterIds: Set<String>, from: Date, to: Date): List<RemoteMovie> {
        return apolloClient.query(MovieShowtimesQuery(theaterId = theaterIds.first(), from = from, to = to))
            .execute().dataAssertNoErrors.movieShowtimeList.edges.mapNotNull { it?.node?.toRemoteMovie() }
    }
}

data class RemoteMovie(
    val id: String,
    val title: String,
    val posterUrl: String?,
)

private fun MovieShowtimesQuery.Data.MovieShowtimeList.Edge.Node.toRemoteMovie() = RemoteMovie(
    id = movie.id,
    title = movie.title,
    posterUrl = null
)

