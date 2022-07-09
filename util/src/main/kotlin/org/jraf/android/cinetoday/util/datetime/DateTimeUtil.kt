/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2021-present Benoit 'BoD' Lubek (BoD@JRAF.org)
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
package org.jraf.android.cinetoday.util.datetime

import java.text.DateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Date.atMidnight(): Date = Calendar.getInstance().apply {
    time = this@atMidnight
    this[Calendar.HOUR_OF_DAY] = 0
    this[Calendar.MINUTE] = 0
    this[Calendar.SECOND] = 0
    this[Calendar.MILLISECOND] = 0
}.time


fun Date.nextDay(): Date = Calendar.getInstance().apply {
    time = this@nextDay
    add(Calendar.DATE, 1)
}.time

fun timestampToLocalDate(timestamp: Long): LocalDate =
    Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.UTC).toLocalDate()

fun LocalDate.toTimestamp(): Long = atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

fun isoDateStringToLocalDate(isoDate: String): LocalDate = LocalDate.parse(isoDate)!!

fun isoDateStringToTimestamp(isoDate: String): Long = isoDateStringToLocalDate(isoDate).toTimestamp()

fun formatHourMinute(date: Date, showtimesIn24HFormat: Boolean): String {
    return if (!showtimesIn24HFormat) {
        DateFormat.getTimeInstance(DateFormat.SHORT).format(date)
    } else {
        DateFormat.getTimeInstance(DateFormat.SHORT, Locale.FRANCE).format(date)
    }
}
