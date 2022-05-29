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
package org.jraf.android.cinetoday.domain.movie

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jraf.android.cinetoday.domain.theater.Theater
import org.jraf.android.cinetoday.domain.theater.TheaterRepository
import org.jraf.android.cinetoday.util.datetime.atMidnight
import org.jraf.android.cinetoday.util.datetime.nextDay
import java.util.Date
import javax.inject.Inject

class GetMovieListForTodayUseCase @Inject constructor(
    private val theaterRepository: TheaterRepository,
    private val movieRepository: MovieRepository,
) {
    operator fun invoke(coroutineScope: CoroutineScope): Flow<Map<Theater, List<Movie>>> {
        return theaterRepository.getFavorites().map { theaterList ->
            val theaterById = theaterList.associateBy { it.id }

            val todayAtMidnight = Date().atMidnight()
            val tomorrowAtMidnight = todayAtMidnight.nextDay()
            movieRepository.fetchMovies(
                theaterIds = theaterList.map { it.id }.toSet(),
                from = todayAtMidnight,
                to = tomorrowAtMidnight,
                coroutineScope = coroutineScope,
            ).mapKeys { theaterById[it.key]!! }
        }
    }
}
