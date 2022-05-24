package org.jraf.android.cinetoday.repository

import org.jraf.android.cinetoday.api.RemoteTheater
import org.jraf.android.cinetoday.api.TheaterRemoteSource
import javax.inject.Inject

interface TheaterRepository {
    suspend fun searchTheaters(search: String): List<RemoteTheater>
}

class TheaterRepositoryImpl @Inject constructor(
    private val theaterRemoteSource: TheaterRemoteSource,
) : TheaterRepository {
    override suspend fun searchTheaters(search: String) = theaterRemoteSource.searchTheaters(search)
}
