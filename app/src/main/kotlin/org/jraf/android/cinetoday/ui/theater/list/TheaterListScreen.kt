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

import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import org.jraf.android.cinetoday.R
import org.jraf.android.cinetoday.domain.theater.model.Theater
import org.jraf.android.cinetoday.ui.theater.item.TheaterItem
import org.jraf.android.cinetoday.ui.theater.search.TheaterSearchActivity

@Composable
fun TheaterListScreen(viewModel: TheaterListViewModel = viewModel()) {
    // TODO empty state
    val favoriteTheaterList by viewModel.favoriteTheaterList.collectAsState(emptyList())
    TheaterList(favoriteTheaterList)
}

@Composable
private fun TheaterList(favoriteTheaterList: List<Theater>) {
    ScalingLazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            ListHeader {
                Text(stringResource(R.string.theater_list_title))
            }
        }
        item {
            val context = LocalContext.current
            Button(
                onClick = { context.startActivity(Intent(context, TheaterSearchActivity::class.java)) },
                modifier = Modifier.padding(PaddingValues(bottom = 16.dp)),
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.theater_list_add),
                    modifier = Modifier
                        .size(ButtonDefaults.DefaultIconSize)
                        .wrapContentSize(align = Alignment.Center),
                )
            }
        }
        items(favoriteTheaterList, key = { it.id }) { theater ->
            TheaterItem(theater, onClick = {})
        }
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND)
@Composable
private fun TheaterListPreview() {
    TheaterList(
        listOf(
            Theater("1", "Theater 1", posterUrl = null, address = "19 avenue de Choisy 75013 Paris"),
        )
    )
}

