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
package org.jraf.android.cinetoday.domain.prefs

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import org.jraf.android.kprefs.Prefs
import javax.inject.Inject

class PreferenceRepository @Inject constructor(@ApplicationContext context: Context) {
    private val mainPrefs = Prefs(context)

    private val showtimesIn24HFormatFlow: Flow<Boolean> by mainPrefs.BooleanFlow(false)
    private var _showtimesIn24HFormat: Boolean by mainPrefs.Boolean(false)

    fun getShowtimesIn24HFormat(): Flow<Boolean> = showtimesIn24HFormatFlow

    fun setShowtimesIn24HFormat(value: Boolean) {
        _showtimesIn24HFormat = value
    }
}
