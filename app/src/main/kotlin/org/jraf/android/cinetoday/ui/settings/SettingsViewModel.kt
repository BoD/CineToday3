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
package org.jraf.android.cinetoday.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jraf.android.cinetoday.domain.movie.usecase.FetchAndSaveMoviesForTodayUseCase
import org.jraf.android.cinetoday.domain.movie.usecase.IsFetchingMoviesUseCase
import org.jraf.android.cinetoday.domain.prefs.usecase.GetLastRefreshDatePreferenceUseCase
import org.jraf.android.cinetoday.domain.prefs.usecase.GetNewReleasesNotificationsPreferenceUseCase
import org.jraf.android.cinetoday.domain.prefs.usecase.GetShowtimesIn24HFormatPreferenceUseCase
import org.jraf.android.cinetoday.domain.prefs.usecase.SetNewReleasesNotificationsPreferenceUseCase
import org.jraf.android.cinetoday.domain.prefs.usecase.SetShowtimesIn24HFormatPreferenceUseCase
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getShowtimesIn24HFormatPreference: GetShowtimesIn24HFormatPreferenceUseCase,
    private val setShowtimesIn24HFormatPreference: SetShowtimesIn24HFormatPreferenceUseCase,

    getNewReleasesNotificationsPreference: GetNewReleasesNotificationsPreferenceUseCase,
    private val setNewReleasesNotificationsPreference: SetNewReleasesNotificationsPreferenceUseCase,

    private val fetchAndSaveMoviesForToday: FetchAndSaveMoviesForTodayUseCase,
    _isFetchingMovies: IsFetchingMoviesUseCase,

    private val getLastRefreshDatePreference: GetLastRefreshDatePreferenceUseCase,
) : ViewModel() {
    val showtimesIn24HFormat: Flow<Boolean> = getShowtimesIn24HFormatPreference()
    fun setShowtimesIn24HFormat(showtimesIn24HFormat: Boolean) = setShowtimesIn24HFormatPreference(showtimesIn24HFormat)

    val newReleasesNotifications: Flow<Boolean> = getNewReleasesNotificationsPreference()
    fun setNewReleasesNotifications(newReleasesNotifications: Boolean) = setNewReleasesNotificationsPreference(newReleasesNotifications)

    val isFetchingMovies: Flow<Boolean> = _isFetchingMovies()

    fun onRefreshNowClick() {
        viewModelScope.launch {
            fetchAndSaveMoviesForToday(viewModelScope)
        }
    }

    val lastRefreshDate: Flow<LocalDateTime?> = getLastRefreshDatePreference()
}
