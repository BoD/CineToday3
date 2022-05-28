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
package org.jraf.android.cinetoday.ui.theater.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jraf.android.cinetoday.repository.Theater
import org.jraf.android.cinetoday.repository.TheaterRepository
import javax.inject.Inject

@HiltViewModel
class TheaterSearchViewModel @Inject constructor(
    private val theaterRepository: TheaterRepository,
) : ViewModel() {
    val searchTerms = MutableStateFlow("")

    val searchResultTheaterList: Flow<List<Theater>> = searchTerms.map { searchTerms ->
        if (searchTerms.isBlank()) {
            emptyList()
        } else {
            theaterRepository.search(searchTerms)
        }
    }

    fun onTheaterClick(theater: Theater, afterTheaterAdded: () -> Unit) {
        viewModelScope.launch {
            theaterRepository.addToFavorites(theater)
            afterTheaterAdded()
        }
    }
}
