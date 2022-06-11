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

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import dagger.hilt.android.HiltAndroidApp
import org.jraf.android.cinetoday.util.logging.initLogging

@HiltAndroidApp
class CineTodayApplication : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        initLogging()
    }

    /**
     * Coil image loader configuration.
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            // Caching
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.5)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("coil_image_cache"))
                    .maxSizePercent(0.05)
                    .build()
            }
            // Crossfade
            .crossfade(true)
            // TODO: do this only in debug release builds
            .logger(DebugLogger())
            // Lower quality but lower memory impact too. Probably a good idea for watches.
            .allowRgb565(true)
            .build()
    }
}
