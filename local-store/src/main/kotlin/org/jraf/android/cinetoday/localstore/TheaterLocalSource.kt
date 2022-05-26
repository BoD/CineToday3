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
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface TheaterLocalSource {
    suspend fun addFavoriteTheater(localTheater: LocalTheater)
    suspend fun removeFavoriteTheater(id: String)
    fun getFavoriteTheaters(): Flow<List<LocalTheater>>
}

class TheaterLocalSourceImpl @Inject constructor(
    private val database: Database,
) : TheaterLocalSource {
    override suspend fun addFavoriteTheater(localTheater: LocalTheater) {
        withContext(Dispatchers.IO) {
            database.theaterQueries.insert(
                id = localTheater.id,
                name = localTheater.name,
                posterUrl = localTheater.posterUrl,
                address = localTheater.address,
            )
        }
    }

    override suspend fun removeFavoriteTheater(id: String) {
        withContext(Dispatchers.IO) {
            database.theaterQueries.delete(id)
        }
    }

    override fun getFavoriteTheaters(): Flow<List<LocalTheater>> {
        return database.theaterQueries.selectAll { id, name, posterUrl, address ->
            LocalTheater(id = id,
                name = name,
                posterUrl = posterUrl,
                address = address)
        }
            .asFlow()
            .mapToList()
    }
}

data class LocalTheater(
    val id: String,
    val name: String,
    val posterUrl: String?,
    val address: String,
)
