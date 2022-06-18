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
package org.jraf.android.cinetoday.ui.perf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import dagger.hilt.android.AndroidEntryPoint
import org.jraf.android.cinetoday.R
import org.jraf.android.cinetoday.ui.theme.CineTodayTheme

@AndroidEntryPoint
class PerfActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PerfScreen()
        }
    }
}

@Composable
private fun PerfScreen() {
    CineTodayTheme {
        Scaffold {
            TheaterList()
        }
    }
}

data class Theater(val id: String, val name: String)

private val theaters = List(42) {
    Theater("$it", "Theater $it")
}

@Composable
private fun TheaterList() {
    ScalingLazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            ListHeader {
                Text(stringResource(R.string.theater_list_title))
            }
        }
        item {
            Button(
                onClick = { },
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
        items(theaters, key = { it.id }) { theater ->
            TheaterItem(theater, onClick = {})
        }
    }
}

@Composable
private fun TheaterItem(theater: Theater, onClick: () -> Unit) {
    Chip(
        modifier = Modifier.fillMaxWidth(),
        colors = ChipDefaults.secondaryChipColors(),
        label = {
            Text(theater.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        secondaryLabel = {
            Text(theater.name, overflow = TextOverflow.Ellipsis)
        },
        icon = {
            Box(
                modifier = Modifier
                    .size(ChipDefaults.LargeIconSize)
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colors.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    tint = MaterialTheme.colors.onPrimary,
                    painter = painterResource(id = R.drawable.ic_theater_48dp),
                    contentDescription = null,
                    modifier = Modifier
                        .size(ChipDefaults.SmallIconSize)
                        .wrapContentSize(align = Alignment.Center)
                )
            }
        },
        onClick = onClick)
}
