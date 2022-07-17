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
package org.jraf.android.cinetoday.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors

private val CineTodayRedAccent = Color(0xFFFF8880)
private val CineTodayRed500 = Color(0xFFF44336)
private val CineTodayRed30 = Color(0xFF4D1410)
private val CineTodayLightGreen400 = Color(0xFF9CCC65)
private val CineTodayWhite = Color.White
private val CineTodayBlack = Color.Black


object CineTodayColor {
    val MovieDefaultBackground = CineTodayRed30
    val ShowtimeTimeBackground = CineTodayWhite
    val ShowtimeTag = CineTodayWhite
    val Confirm = CineTodayLightGreen400
}

val CineTodayColors: Colors = Colors(
    primary = CineTodayRed500,
    primaryVariant = CineTodayRed500,
    secondary = CineTodayRedAccent,
    secondaryVariant = CineTodayRedAccent,
    error = CineTodayRedAccent,
    onPrimary = CineTodayBlack,
)

