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
package org.jraf.android.cinetoday.ui.movie.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import org.jraf.android.cinetoday.domain.movie.model.Movie
import org.jraf.android.cinetoday.domain.movie.usecase.GetMovieWithShowtimesUseCase
import org.jraf.android.cinetoday.domain.prefs.usecase.GetShowtimesIn24HFormatPreferenceUseCase
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    getMovieWithShowtimes: GetMovieWithShowtimesUseCase,
    getShowtimesIn24HFormatPreferenceUseCase: GetShowtimesIn24HFormatPreferenceUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val movie: Flow<Movie> = getMovieWithShowtimes(savedStateHandle[MovieDetailsActivity.EXTRA_MOVIE_ID]!!)
    val showtimesIn24HFormat: Flow<Boolean> = getShowtimesIn24HFormatPreferenceUseCase()
}
