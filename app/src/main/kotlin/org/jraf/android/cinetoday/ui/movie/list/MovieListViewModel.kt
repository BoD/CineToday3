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
package org.jraf.android.cinetoday.ui.movie.list

import android.os.Build
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import org.jraf.android.cinetoday.domain.movie.FetchAndSaveMoviesForTodayUseCase
import org.jraf.android.cinetoday.domain.movie.GetMovieListUseCase
import org.jraf.android.cinetoday.domain.movie.Movie
import org.jraf.android.cinetoday.util.logging.logd
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    fetchAndSaveMoviesForToday: FetchAndSaveMoviesForTodayUseCase,
    getMovieList: GetMovieListUseCase,
) : ViewModel() {
    //    val movieList: Flow<Map<Theater, List<Movie>>> = getMovieListForToday(viewModelScope)
    init {
//        viewModelScope.launch {
//            fetchAndSaveMoviesForToday(viewModelScope)
//        }

        logd("Model=${Build.MODEL} ${Build.BOARD} ${Build.BRAND} ${Build.DEVICE} ${Build.HARDWARE} ${Build.ID} ${Build.MANUFACTURER} ${Build.PRODUCT} ${Build.SERIAL} ${Build.TAGS} ${Build.TYPE} ${Build.USER}")
    }

    val movieList: Flow<List<Movie>> = getMovieList()
}
