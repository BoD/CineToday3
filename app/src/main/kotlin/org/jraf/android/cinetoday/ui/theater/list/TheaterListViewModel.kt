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
package org.jraf.android.cinetoday.ui.theater.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jraf.android.cinetoday.domain.theater.model.Theater
import org.jraf.android.cinetoday.domain.theater.usecase.DeleteTheaterUseCase
import org.jraf.android.cinetoday.domain.theater.usecase.GetTheaterFavoriteListUseCase
import javax.inject.Inject

@HiltViewModel
class TheaterListViewModel @Inject constructor(
    getTheaterFavoriteList: GetTheaterFavoriteListUseCase,
    private val deleteTheater: DeleteTheaterUseCase,
) : ViewModel() {
    val favoriteTheaterList: Flow<List<Theater>> = getTheaterFavoriteList()

    fun deleteTheater(deleteTheater: Theater) {
        viewModelScope.launch {
            deleteTheater(deleteTheater.id)
        }
    }
}
