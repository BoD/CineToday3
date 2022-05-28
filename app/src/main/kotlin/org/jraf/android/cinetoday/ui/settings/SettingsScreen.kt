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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Text
import org.jraf.android.cinetoday.util.logging.WEAR_PREVIEW_API_LEVEL
import org.jraf.android.cinetoday.util.logging.WEAR_PREVIEW_BACKGROUND_COLOR_BLACK
import org.jraf.android.cinetoday.util.logging.WEAR_PREVIEW_DEVICE_HEIGHT_DP
import org.jraf.android.cinetoday.util.logging.WEAR_PREVIEW_DEVICE_WIDTH_DP
import org.jraf.android.cinetoday.util.logging.WEAR_PREVIEW_SHOW_BACKGROUND
import org.jraf.android.cinetoday.util.logging.WEAR_PREVIEW_UI_MODE

@Composable
fun SettingsScreen() {
    Text("Settings", modifier = Modifier
        .fillMaxSize()
//        .background(Color.Green)
    )
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
private fun SettingsScreenPreview() {
    SettingsScreen()
}
