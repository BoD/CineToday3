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
package org.jraf.android.cinetoday.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.jraf.android.cinetoday.data.movie.MovieRepositoryImpl
import org.jraf.android.cinetoday.data.prefs.PreferenceRepositoryImpl
import org.jraf.android.cinetoday.data.theater.TheaterRepositoryImpl
import org.jraf.android.cinetoday.domain.movie.MovieRepository
import org.jraf.android.cinetoday.domain.prefs.PreferenceRepository
import org.jraf.android.cinetoday.domain.theater.TheaterRepository

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun TheaterRepository(
        TheaterRepository: TheaterRepositoryImpl,
    ): TheaterRepository

    @Binds
    fun MovieRepository(
        MovieRepository: MovieRepositoryImpl,
    ): MovieRepository

    @Binds
    fun PreferenceRepository(
        PreferenceRepository: PreferenceRepositoryImpl,
    ): PreferenceRepository
}