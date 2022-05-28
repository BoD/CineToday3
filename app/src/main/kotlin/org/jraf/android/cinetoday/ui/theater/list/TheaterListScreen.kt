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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.jraf.android.cinetoday.R
import org.jraf.android.cinetoday.repository.Theater
import org.jraf.android.cinetoday.util.logging.WEAR_PREVIEW_API_LEVEL
import org.jraf.android.cinetoday.util.logging.WEAR_PREVIEW_BACKGROUND_COLOR_BLACK
import org.jraf.android.cinetoday.util.logging.WEAR_PREVIEW_DEVICE_HEIGHT_DP
import org.jraf.android.cinetoday.util.logging.WEAR_PREVIEW_DEVICE_WIDTH_DP
import org.jraf.android.cinetoday.util.logging.WEAR_PREVIEW_SHOW_BACKGROUND
import org.jraf.android.cinetoday.util.logging.WEAR_PREVIEW_UI_MODE
import org.jraf.android.cinetoday.util.logging.logd

@Composable
fun TheaterListScreen() {
    val viewModel: TheaterListViewModel = viewModel()
    val favoriteTheaterList by viewModel.favoriteTheaterList.collectAsState(emptyList())

    ScalingLazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            ListHeader {
                Text(stringResource(R.string.theaters_title))
            }
        }
        item {
            Button(
                onClick = { logd("Add theater") },
                modifier = Modifier.padding(PaddingValues(bottom = 16.dp)),
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.theaters_add),
                    modifier = Modifier
                        .size(ButtonDefaults.DefaultIconSize)
                        .wrapContentSize(align = Alignment.Center),
                )
            }
        }
        items(favoriteTheaterList) { theater ->
            logd(theater)
            TheaterItem(theater)
        }
    }
}

@Composable
private fun TheaterItem(theater: Theater) {
    Chip(
        modifier = Modifier.fillMaxWidth(),
        colors = ChipDefaults.secondaryChipColors(),
        label = {
            Text(theater.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        secondaryLabel = {
            Text(theater.address, overflow = TextOverflow.Ellipsis)
        },
        icon = {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(theater.posterUrl)
                    .crossfade(true)
                    .build(),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .size(ChipDefaults.IconSize)
                    .clip(CircleShape)
                    .wrapContentSize(align = Alignment.Center),
            )
        },
        onClick = { logd("Theater clicked") })
}

@Preview(
    widthDp = WEAR_PREVIEW_DEVICE_WIDTH_DP,
    apiLevel = WEAR_PREVIEW_API_LEVEL,
    uiMode = WEAR_PREVIEW_UI_MODE,
    backgroundColor = WEAR_PREVIEW_BACKGROUND_COLOR_BLACK,
    showBackground = WEAR_PREVIEW_SHOW_BACKGROUND
)
@Composable
private fun TheaterItemPreview() {
    TheaterItem(Theater("1", "Theater 1", posterUrl = null, address = "19 avenue de Choisy 75013 Paris"))
}

@Preview(
    widthDp = WEAR_PREVIEW_DEVICE_WIDTH_DP,
    heightDp = WEAR_PREVIEW_DEVICE_HEIGHT_DP,
    apiLevel = WEAR_PREVIEW_API_LEVEL,
    uiMode = WEAR_PREVIEW_UI_MODE,
    backgroundColor = WEAR_PREVIEW_BACKGROUND_COLOR_BLACK,
    showBackground = WEAR_PREVIEW_SHOW_BACKGROUND
)
@Composable
private fun TheaterListScreenPreview() {
    TheaterListScreen()
}

