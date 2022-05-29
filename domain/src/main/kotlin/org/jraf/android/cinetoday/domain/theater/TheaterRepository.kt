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
package org.jraf.android.cinetoday.domain.theater

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jraf.android.cinetoday.api.RemoteTheater
import org.jraf.android.cinetoday.api.TheaterRemoteSource
import org.jraf.android.cinetoday.localstore.LocalTheater
import org.jraf.android.cinetoday.localstore.TheaterLocalSource
import javax.inject.Inject

class TheaterRepository @Inject constructor(
    private val theaterRemoteSource: TheaterRemoteSource,
    private val theaterLocalSource: TheaterLocalSource,
) {
    suspend fun search(search: String): List<Theater> = theaterRemoteSource.searchTheaters(search).map { it.toTheater() }

    suspend fun addToFavorites(theater: Theater) {
        theaterLocalSource.addFavoriteTheater(LocalTheater(id = theater.id, name = theater.name, posterUrl = theater.posterUrl, address = theater.address))
    }

    suspend fun removeFromFavorites(theaterId: String) {
        theaterLocalSource.removeFavoriteTheater(theaterId)
    }

    fun getFavorites(): Flow<List<Theater>> {
        return theaterLocalSource.getFavoriteTheaters().map { list -> list.map { localTheater -> localTheater.toTheater() } }
    }
}

private fun RemoteTheater.toTheater() = Theater(id = id, name = name, posterUrl = posterUrl, address = address)
private fun LocalTheater.toTheater() = Theater(id = id, name = name, posterUrl = posterUrl, address = address)

data class Theater(
    val id: String,
    val name: String,
    val posterUrl: String?,
    val address: String,
)
