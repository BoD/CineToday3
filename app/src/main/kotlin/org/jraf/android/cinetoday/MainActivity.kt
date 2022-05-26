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
package org.jraf.android.cinetoday

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.jraf.android.cinetoday.repository.Theater
import org.jraf.android.cinetoday.repository.TheaterRepository
import org.jraf.android.cinetoday.util.logging.logd
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var theaterRepository: TheaterRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        runBlocking {
            val theaters: List<Theater> = theaterRepository.search("trappes")
            logd(theaters)
            theaterRepository.addToFavorites(theaters[0])
            val favorites: List<Theater> = theaterRepository.getFavorites().first()
            logd("Favorites: $favorites")
            theaterRepository.removeFromFavorites(favorites[0].id)
            val favoritesAfter: List<Theater> = theaterRepository.getFavorites().first()
            logd("Favorites after: $favoritesAfter")
        }
    }
}
