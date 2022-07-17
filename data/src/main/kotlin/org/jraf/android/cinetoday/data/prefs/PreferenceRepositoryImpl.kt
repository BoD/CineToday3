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
package org.jraf.android.cinetoday.data.prefs

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.jraf.android.cinetoday.domain.prefs.PreferenceRepository
import org.jraf.android.cinetoday.util.datetime.timestampToLocalDateTime
import org.jraf.android.kprefs.Prefs
import java.time.LocalDateTime
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(@ApplicationContext context: Context) : PreferenceRepository {
    private val mainPrefs = Prefs(context)

    private val showtimesIn24HFormat: MutableStateFlow<Boolean> by mainPrefs.BooleanFlow(true)
    private val newReleasesNotifications: MutableStateFlow<Boolean> by mainPrefs.BooleanFlow(true)
    private val lastRefreshDate: MutableStateFlow<Long?> by mainPrefs.LongFlow()

    override fun getShowtimesIn24HFormat(): Flow<Boolean> = showtimesIn24HFormat
    override fun setShowtimesIn24HFormat(value: Boolean) {
        showtimesIn24HFormat.value = value
    }

    override fun getNewReleasesNotifications(): Flow<Boolean> = newReleasesNotifications
    override fun setNewReleasesNotifications(value: Boolean) {
        newReleasesNotifications.value = value
    }

    override fun updateLastRefreshDate() {
        lastRefreshDate.value = System.currentTimeMillis()
    }

    override fun getLastRefreshDate(): Flow<LocalDateTime?> = lastRefreshDate.map { lastRefreshDate ->
        lastRefreshDate?.let { timestamp -> timestampToLocalDateTime(timestamp) }
    }
}
