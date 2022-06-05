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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

interface MovieRemoteSource {
    suspend fun fetchMovies(theaterIds: Set<String>, from: Date, to: Date, coroutineScope: CoroutineScope): Map<String, List<RemoteMovie>>
}

class MovieRemoteSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient,
) : MovieRemoteSource {
    override suspend fun fetchMovies(theaterIds: Set<String>, from: Date, to: Date, coroutineScope: CoroutineScope): Map<String, List<RemoteMovie>> {
        val result = mutableMapOf<String, List<RemoteMovie>>()
        val jobs = mutableListOf<Job>()
        for (theaterId in theaterIds) {
            jobs += coroutineScope.launch {
                result[theaterId] = apolloClient
                    .query(MovieWithShowtimesListQuery(theaterId = theaterId, from = from, to = to))
                    .execute()
                    .dataAssertNoErrors.movieShowtimeList.edges.mapNotNull { it?.node?.toRemoteMovie() }
            }
        }
        jobs.joinAll()
        return result
    }
}

data class RemoteMovie(
    val id: String,
    val title: String,
    val posterUrl: String?,
    val showtimes: List<RemoteShowtime>,
)

data class RemoteShowtime(
    val id: String,
    val startsAt: Date,
    val projection: List<String>,
    val languageVersion: String?,
)

private fun MovieWithShowtimesListQuery.Data.MovieShowtimeList.Edge.Node.toRemoteMovie() = RemoteMovie(
    id = movie.id,
    title = movie.title,
    posterUrl = movie.poster?.url,
    showtimes = showtimes.mapNotNull { it?.toRemoteShowtime() }
)

private fun MovieWithShowtimesListQuery.Data.MovieShowtimeList.Edge.Node.Showtime.toRemoteShowtime() = RemoteShowtime(
    id = id,
    startsAt = startsAt,
    projection = projection?.mapNotNull { it?.name } ?: emptyList(),
    languageVersion = diffusionVersion?.name
)

