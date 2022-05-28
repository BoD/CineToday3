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
import javax.inject.Inject

interface TheaterRemoteSource {
    suspend fun searchTheaters(search: String): List<RemoteTheater>
}

class TheaterRemoteSourceImpl @Inject constructor(
    private val apolloClient: ApolloClient,
) : TheaterRemoteSource {
    override suspend fun searchTheaters(search: String): List<RemoteTheater> {
        return apolloClient.query(TheaterSearchQuery(search)).execute().dataAssertNoErrors.theaterList.edges.mapNotNull { it?.node?.toRemoteTheater() }
    }
}

data class RemoteTheater(
    val id: String,
    val name: String,
    val posterUrl: String?,
    val address: String,
)

private fun TheaterSearchQuery.Data.TheaterList.Edge.Node.toRemoteTheater() = RemoteTheater(
    id = id,
    name = name,
    posterUrl = poster?.url,
    address = (location.address?.let { "$it - " } ?: "") + location.city
)
