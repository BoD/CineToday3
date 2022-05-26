package org.jraf.android.cinetoday.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jraf.android.cinetoday.api.RemoteTheater
import org.jraf.android.cinetoday.api.TheaterRemoteSource
import org.jraf.android.cinetoday.localstore.LocalTheater
import org.jraf.android.cinetoday.localstore.TheaterLocalSource
import javax.inject.Inject

interface TheaterRepository {
    suspend fun search(search: String): List<Theater>
    suspend fun addToFavorites(theater: Theater)
    suspend fun removeFromFavorites(theaterId: String)
    fun getFavorites(): Flow<List<Theater>>
}

class TheaterRepositoryImpl @Inject constructor(
    private val theaterRemoteSource: TheaterRemoteSource,
    private val theaterLocalSource: TheaterLocalSource,
) : TheaterRepository {
    override suspend fun search(search: String): List<Theater> = theaterRemoteSource.searchTheaters(search).map { it.toTheater() }

    override suspend fun addToFavorites(theater: Theater) {
        theaterLocalSource.addFavoriteTheater(LocalTheater(id = theater.id, name = theater.name, posterUrl = theater.posterUrl, address = theater.address))
    }

    override suspend fun removeFromFavorites(theaterId: String) {
        theaterLocalSource.removeFavoriteTheater(theaterId)
    }

    override fun getFavorites(): Flow<List<Theater>> {
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
