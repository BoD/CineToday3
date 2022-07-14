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
package org.jraf.android.cinetoday.domain.movie.model

import org.jraf.android.cinetoday.util.datetime.formatHourMinute
import java.util.Date

data class Showtime(
    val id: String,
    val theaterId: String,
    val theaterName: String,
    val startsAt: Date,
    val projection: List<String>,
    val languageVersion: String?,
) {
    fun isTooLate(): Boolean {
        return Date().after(startsAt)
    }

    fun startsAtFormatted(showtimesIn24HFormat: Boolean): String {
        return formatHourMinute(startsAt, showtimesIn24HFormat)
    }

    val is3D: Boolean by lazy { projection.any { it.contains("3d", ignoreCase = true) } }
    val isImax: Boolean by lazy { projection.any { it.contains("imax", ignoreCase = true) } }
    val isDubbed: Boolean by lazy { languageVersion == "DUBBED" }
}
