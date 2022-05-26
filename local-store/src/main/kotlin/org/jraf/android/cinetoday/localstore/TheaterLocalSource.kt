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
