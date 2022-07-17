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
package org.jraf.android.cinetoday.ui.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.ToggleChipDefaults
import androidx.wear.compose.material.rememberScalingLazyListState
import com.google.android.horologist.compose.layout.fadeAwayScalingLazyList
import com.google.android.horologist.compose.navscaffold.ExperimentalHorologistComposeLayoutApi
import org.jraf.android.cinetoday.R
import org.jraf.android.cinetoday.util.datetime.formatLocalDateTime
import java.time.LocalDateTime

@OptIn(ExperimentalHorologistComposeLayoutApi::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val scalingLazyListState = rememberScalingLazyListState()
    val showtimesIn24HFormat: Boolean by viewModel.showtimesIn24HFormat.collectAsState(true)
    val newReleasesNotifications: Boolean by viewModel.newReleasesNotifications.collectAsState(true)
    val isFetchingMovies: Boolean by viewModel.isFetchingMovies.collectAsState(false)
    val lastRefreshDate: LocalDateTime? by viewModel.lastRefreshDate.collectAsState(null)

    Scaffold(
        timeText = {
            TimeText(Modifier.fadeAwayScalingLazyList { scalingLazyListState })
        }
    ) {
        SettingsList(
            scalingLazyListState = scalingLazyListState,
            onShowtimesIn24HFormatChange = { value -> viewModel.setShowtimesIn24HFormat(value) },
            newReleasesNotifications = newReleasesNotifications,
            showtimesIn24HFormat = showtimesIn24HFormat,
            onNewReleasesNotificationsChange = { value -> viewModel.setNewReleasesNotifications(value) },
            isFetchingMovies = isFetchingMovies,
            onRefreshNowClick = { viewModel.onRefreshNowClick() },
            lastRefreshDate = lastRefreshDate,
        )
    }
}

@Composable
private fun SettingsList(
    scalingLazyListState: ScalingLazyListState,
    showtimesIn24HFormat: Boolean,
    newReleasesNotifications: Boolean,
    onShowtimesIn24HFormatChange: (Boolean) -> Unit,
    onNewReleasesNotificationsChange: (Boolean) -> Unit,
    isFetchingMovies: Boolean,
    onRefreshNowClick: () -> Unit,
    lastRefreshDate: LocalDateTime?,
) {
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        state = scalingLazyListState
    ) {
        item {
            ListHeader {
                Text(stringResource(R.string.settings_title))
            }
        }

        item {
            ToggleChip(
                modifier = Modifier.fillMaxWidth(),
                checked = newReleasesNotifications,
                onCheckedChange = { onNewReleasesNotificationsChange(!newReleasesNotifications) },
                label = {
                    Text(stringResource(R.string.settings_notifications))
                },
                toggleControl = {
                    // TODO have this animated somehow
                    // See https://kotlinlang.slack.com/archives/C02GBABJUAF/p1657817096543849
                    Icon(
                        imageVector = ToggleChipDefaults.switchIcon(
                            checked = newReleasesNotifications
                        ),
                        contentDescription = stringResource(if (newReleasesNotifications) R.string.settings_on else R.string.settings_off)
                    )
                }
            )
        }

        item {
            ToggleChip(
                modifier = Modifier.fillMaxWidth(),
                checked = showtimesIn24HFormat,
                onCheckedChange = {
                    onShowtimesIn24HFormatChange(!showtimesIn24HFormat)
                },
                label = {
                    Text(stringResource(R.string.settings_24HFormat))
                },
                toggleControl = {
                    Icon(
                        imageVector = ToggleChipDefaults.switchIcon(
                            checked = showtimesIn24HFormat
                        ),
                        contentDescription = stringResource(if (showtimesIn24HFormat) R.string.settings_on else R.string.settings_off)
                    )
                }
            )
        }

        item {
            Chip(
                modifier = Modifier.fillMaxWidth(),
                colors = ChipDefaults.secondaryChipColors(),
                label = {
                    Text(text = stringResource(if (isFetchingMovies) R.string.settings_refreshing else R.string.settings_refreshNow))
                },
                secondaryLabel = if (isFetchingMovies) {
                    null
                } else {
                    lastRefreshDate?.let { lastRefreshDate ->
                        {
                            Text(stringResource(R.string.settings_lastRefreshDate, formatLocalDateTime(lastRefreshDate)))
                        }
                    }
                },
                icon = if (!isFetchingMovies) {
                    null
                } else {
                    { CircularProgressIndicator() }
                },
                onClick = onRefreshNowClick,
                enabled = !isFetchingMovies,
            )
        }

        item {
            Chip(
                modifier = Modifier.fillMaxWidth(),
                colors = ChipDefaults.secondaryChipColors(),
                label = {
                    Text(text = stringResource(R.string.settings_about))
                },
                onClick = {

                }
            )
        }
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND)
@Composable
private fun SettingsListPreview() {
    SettingsList(
        scalingLazyListState = rememberScalingLazyListState(),
        showtimesIn24HFormat = false,
        newReleasesNotifications = true,
        onShowtimesIn24HFormatChange = {},
        onNewReleasesNotificationsChange = {},
        isFetchingMovies = true,
        onRefreshNowClick = {},
        lastRefreshDate = null,
    )
}
