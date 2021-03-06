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

import android.content.Context
import org.jraf.android.cinetoday.util.R
import java.text.DateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
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

fun Date.at8(): Date = Calendar.getInstance().apply {
    time = this@at8
    this[Calendar.HOUR_OF_DAY] = 8
    this[Calendar.MINUTE] = 0
    this[Calendar.SECOND] = 0
    this[Calendar.MILLISECOND] = 0
}.time


fun Date.nextDay(): Date = Calendar.getInstance().apply {
    time = this@nextDay
    add(Calendar.DATE, 1)
}.time

fun timestampToLocalDate(timestamp: Long): LocalDate =
    Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.systemDefault()).toLocalDate()

fun timestampToLocalDateTime(timestamp: Long): LocalDateTime =
    Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.systemDefault()).toLocalDateTime()


fun LocalDate.toTimestamp(): Long {
    val systemOffset = OffsetDateTime.now().offset
    return atStartOfDay().toInstant(systemOffset).toEpochMilli()
}

fun isoDateStringToLocalDate(isoDate: String): LocalDate = LocalDate.parse(isoDate)!!

fun isoDateStringToTimestamp(isoDate: String): Long = isoDateStringToLocalDate(isoDate).toTimestamp()

fun formatHourMinute(date: Date, showtimesIn24HFormat: Boolean): String {
    return if (!showtimesIn24HFormat) {
        DateFormat.getTimeInstance(DateFormat.SHORT).format(date)
    } else {
        DateFormat.getTimeInstance(DateFormat.SHORT, Locale.FRANCE).format(date)
    }
}

context(Context)
fun formatDurationHourMinute(durationMinutes: Int): String {
    val hours = durationMinutes / 60
    val minutes = durationMinutes % 60
    val components = mutableListOf<String>()
    if (hours > 0) {
        components += "$hours ${getString(if (hours == 1) R.string.hour else R.string.hours)}"
    }
    if (minutes > 0) {
        components += "$minutes ${getString(if (minutes == 1) R.string.minute else R.string.minutes)}"
    }
    return components.joinToString(" ")
}

fun formatLocalDateTime(localDateTime: LocalDateTime): String {
    return localDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
}

fun minutesUntilTomorrowAt8(): Long {
    val now = Date()
    val tomorrowAt8 = now.atMidnight().nextDay().at8()
    return (tomorrowAt8.time - now.time) / 1000 / 60
}
