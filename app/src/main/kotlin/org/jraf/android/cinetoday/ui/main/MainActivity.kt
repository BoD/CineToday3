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
package org.jraf.android.cinetoday.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import dagger.hilt.android.AndroidEntryPoint
import org.jraf.android.cinetoday.ui.theme.CineTodayTheme
import kotlin.random.Random

private const val PAGE_COUNT = 3

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
private fun MainScreen() {
    CineTodayTheme {
        Scaffold {
            MainScreenContent()
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun MainScreenContent() {
    val pagerState: PagerState = rememberPagerState()
    HorizontalPager(
        count = PAGE_COUNT,
        modifier = Modifier.fillMaxSize(),
        state = pagerState,
    ) { page ->
        when (page) {
            0 -> PageScreen(0)
            1 -> PageScreen(1)
            2 -> PageScreen(2)
        }
    }
}

@Composable
fun PageScreen(pageIndex: Int) {
    Text(text = pageIndex.toString(), textAlign = TextAlign.Center, modifier = Modifier
        .fillMaxSize()
        .background(Color(Random.nextLong())))
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen()
}
