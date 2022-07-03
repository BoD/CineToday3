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

import android.content.Context
import androidx.annotation.ColorInt
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Target
import androidx.palette.graphics.get
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.apollographql.apollo3.ApolloClient
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jraf.android.cinetoday.util.bitmap.toBitmap
import org.jraf.android.cinetoday.util.logging.logd
import org.jraf.android.cinetoday.util.logging.logw
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.collections.set
import kotlin.coroutines.resume

interface MovieRemoteSource {
    suspend fun fetchMovies(theaterIds: Set<String>, from: Date, to: Date, coroutineScope: CoroutineScope): Map<String, List<RemoteMovie>>
}

class MovieRemoteSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apolloClient: ApolloClient,
) : MovieRemoteSource {
    override suspend fun fetchMovies(theaterIds: Set<String>, from: Date, to: Date, coroutineScope: CoroutineScope): Map<String, List<RemoteMovie>> {
        val result = mutableMapOf<String, List<RemoteMovie>>()
        val moviesForTheaterJobs = mutableListOf<Job>()
        for (theaterId in theaterIds) {
            moviesForTheaterJobs += coroutineScope.launch {
                val movieNodes: List<MovieWithShowtimesListQuery.Data.MovieShowtimeList.Edge.Node> = apolloClient
                    .query(MovieWithShowtimesListQuery(theaterId = theaterId, from = from, to = to))
                    .execute()
                    .dataAssertNoErrors.movieShowtimeList.edges
                    .filterNotNull() // GraphQL . . .
                    .filterNot { it.node.movie == null } // GraphQL . . .
                    .map { it.node }

                result[theaterId] = movieNodes.map { movieNode ->
                    async {
                        val palette = movieNode.movie!!.poster?.url?.let { downloadMoviePosterAndComputePalette(it) }
                        val colorDark = palette?.get(Target.DARK_VIBRANT)?.rgb
                        val colorLight = palette?.get(Target.LIGHT_MUTED)?.rgb
                        movieNode.toRemoteMovie(colorDark = colorDark, colorLight = colorLight)
                    }
                }.awaitAll()
            }
        }
        moviesForTheaterJobs.joinAll()
        return result
    }

    private suspend fun downloadMoviePosterAndComputePalette(moviePosterUrl: String): Palette? {
        logd("Downloading movie poster: $moviePosterUrl")
        return suspendCancellableCoroutine { continuation ->
            val request = ImageRequest.Builder(context)
                .data(moviePosterUrl)
                // Disable hardware bitmaps as Palette needs to read the image's pixels
                .allowHardware(false)
                .memoryCachePolicy(CachePolicy.DISABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .listener(
                    onSuccess = { _, result ->
                        logd("MovieRemoteSourceImpl", "Generating palette for poster: $moviePosterUrl")
                        val bitmap = result.drawable.toBitmap()!!
                        val palette = Palette.from(bitmap).generate()
                        continuation.resume(palette)
                    },
                    onError = { _, result ->
                        logw("MovieRemoteSourceImpl", "Could not load movie poster moviePosterUrl=$moviePosterUrl result=$result")
                        continuation.resume(null)
                    }
                )
                .build()
            context.imageLoader.enqueue(request)
        }
    }
}

data class RemoteMovie(
    val id: String,
    val title: String,
    val posterUrl: String?,
    val releaseDate: String?,
    val showtimes: List<RemoteShowtime>,
    val weeklyTheatersCount: Long,
    val directors: List<String>,
    @ColorInt val colorDark: Int?,
    @ColorInt val colorLight: Int?,
    val genres: List<String>,
    val actors: List<String>,
    val synopsis: String?,
    val runtimeMinutes: Int,
    val originalTitle: String,
)

data class RemoteShowtime(
    val id: String,
    val startsAt: Date,
    val projection: List<String>,
    val languageVersion: String?,
)

private fun MovieWithShowtimesListQuery.Data.MovieShowtimeList.Edge.Node.toRemoteMovie(colorDark: Int?, colorLight: Int?) = RemoteMovie(
    id = movie!!.id,
    title = movie.title,
    posterUrl = movie.poster?.url,
    releaseDate = movie.releases.firstOrNull()?.releaseDate?.date,
    showtimes = showtimes.mapNotNull { it?.toRemoteShowtime() },
    weeklyTheatersCount = movie.weeklyTheatersCount.toLong(),
    colorDark = colorDark,
    colorLight = colorLight,
    directors = movie.credits.edges.mapNotNull { it?.node?.person?.stringValue },
    genres = movie.genres.mapNotNull { it?.name?.replace('_', ' ')?.replaceFirstChar { c -> if (c.isLowerCase()) c.titlecase(Locale.ROOT) else c.toString() } },
    actors = movie.cast.edges.mapNotNull {
        it?.node?.actor?.stringValue
            ?: it?.node?.originalVoiceActor?.stringValue
            ?: it?.node?.voiceActor?.stringValue
    },
    synopsis = movie.synopsis,
    runtimeMinutes = movie.runtime.toInt(),
    originalTitle = movie.originalTitle
)

private fun MovieWithShowtimesListQuery.Data.MovieShowtimeList.Edge.Node.Showtime.toRemoteShowtime() = RemoteShowtime(
    id = id,
    startsAt = startsAt,
    projection = projection?.mapNotNull { it?.name } ?: emptyList(),
    languageVersion = diffusionVersion?.name
)

