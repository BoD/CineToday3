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

import android.app.RemoteInput
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.ContentAlpha
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.LocalContentAlpha
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.input.wearableExtender
import dagger.hilt.android.AndroidEntryPoint
import org.jraf.android.cinetoday.R
import org.jraf.android.cinetoday.ui.theater.item.TheaterItem
import org.jraf.android.cinetoday.ui.theme.CineTodayTheme
import org.jraf.android.cinetoday.util.logging.WEAR_PREVIEW_API_LEVEL
import org.jraf.android.cinetoday.util.logging.WEAR_PREVIEW_BACKGROUND_COLOR_BLACK
import org.jraf.android.cinetoday.util.logging.WEAR_PREVIEW_DEVICE_HEIGHT_DP
import org.jraf.android.cinetoday.util.logging.WEAR_PREVIEW_DEVICE_WIDTH_DP
import org.jraf.android.cinetoday.util.logging.WEAR_PREVIEW_SHOW_BACKGROUND
import org.jraf.android.cinetoday.util.logging.WEAR_PREVIEW_UI_MODE

@AndroidEntryPoint
class TheaterSearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TheaterSearchScreen(afterTheaterAdded = { finish() })
        }
    }
}

@Composable
private fun TheaterSearchScreen(afterTheaterAdded: () -> Unit) {
    CineTodayTheme {
        Scaffold {
            TheaterSearchContent(afterTheaterAdded = afterTheaterAdded)
        }
    }
}

@Composable
private fun TheaterSearchContent(afterTheaterAdded: () -> Unit) {
    val viewModel: TheaterSearchViewModel = viewModel()
    val searchResultTheaterList by viewModel.searchResultTheaterList.collectAsState(emptyList())
    val searchTerms by viewModel.searchTerms.collectAsState("")
    val keySearchTerms = "search_terms"

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { data ->
                val typedSearchTerms = RemoteInput.getResultsFromIntent(data).getCharSequence(keySearchTerms)?.toString() ?: ""
                viewModel.searchTerms.value = typedSearchTerms
            }
        }

    val hasEnteredSearchTerms = searchTerms.isNotBlank()
    ScalingLazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            ListHeader {
                Text(stringResource(R.string.theater_search_title))
            }
        }
        item {
            val placeHolder = stringResource(R.string.theater_search_placeholder)
            Column {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .border(shape = MaterialTheme.shapes.small, width = 1.dp, color = MaterialTheme.colors.primary)
                ) {
                    CompositionLocalProvider(LocalContentAlpha provides if (hasEnteredSearchTerms) ContentAlpha.high else ContentAlpha.medium) {
                        Text(
                            text = if (hasEnteredSearchTerms) searchTerms else placeHolder,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(shape = MaterialTheme.shapes.small)
                                .clickable(
                                    onClick = {
                                        val intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
                                        val remoteInputs = listOf(
                                            RemoteInput
                                                .Builder(keySearchTerms)
                                                .setLabel(placeHolder)
                                                .wearableExtender {
                                                    setEmojisAllowed(false)
                                                    setInputActionType(EditorInfo.IME_ACTION_SEARCH)
                                                }
                                                .build()
                                        )
                                        RemoteInputIntentHelper.putRemoteInputsExtra(intent, remoteInputs)
                                        launcher.launch(intent)
                                    }
                                )
                                .padding(8.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        if (searchResultTheaterList.isEmpty() && hasEnteredSearchTerms) {
            item {
                Text(stringResource(R.string.theater_search_noResults), modifier = Modifier.padding(16.dp))
            }
        } else {
            items(searchResultTheaterList) { theater ->
                TheaterItem(theater, onClick = {
                    viewModel.onTheaterClick(theater, afterTheaterAdded)
                })
            }
        }
    }
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
private fun MainScreenPreview() {
    TheaterSearchScreen(afterTheaterAdded = {})
}
