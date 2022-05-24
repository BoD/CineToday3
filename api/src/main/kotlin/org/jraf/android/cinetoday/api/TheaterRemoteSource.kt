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
    address = location.address + " " + location.zip + " " + location.city
)
