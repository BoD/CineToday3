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
@file:Suppress("NOTHING_TO_INLINE")

package org.jraf.android.cinetoday.util.logging

import timber.log.Timber

fun initLogging() {
    Timber.plant(Timber.DebugTree())
}

inline fun logd(tag: String, message: String) {
    Timber.tag(tag)
    Timber.d(message)
}

inline fun logd(message: String) {
    Timber.d(message)
}

inline fun logd(any: Any) {
    Timber.d(any.toString())
}

inline fun logd(throwable: Throwable) {
    Timber.d(throwable)
}

inline fun logd(throwable: Throwable, message: String) {
    Timber.d(throwable, message)
}

inline fun logd(throwable: Throwable, any: Any) {
    Timber.d(throwable, any.toString())
}
