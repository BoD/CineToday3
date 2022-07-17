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
package org.jraf.android.cinetoday.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jraf.android.cinetoday.domain.movie.usecase.FetchAndSaveMoviesForTodayUseCase
import org.jraf.android.cinetoday.util.datetime.minutesUntilTomorrowAt8
import org.jraf.android.cinetoday.util.logging.logd
import java.util.concurrent.TimeUnit

@HiltWorker
class LoadMoviesWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val fetchAndSaveMoviesForTodayUseCase: FetchAndSaveMoviesForTodayUseCase,
) : CoroutineWorker(appContext, workerParams) {
    companion object {
        internal val TAG = LoadMoviesWorker::class.java.simpleName
    }

    override suspend fun doWork(): Result {
        logd("doWork called")
        return try {
            fetchAndSaveMoviesForTodayUseCase(CoroutineScope(Dispatchers.IO))
            logd("doWork returning success")
            Result.success()
        } catch (e: Exception) {
            logd("doWork returning retry")
            Result.retry()
        }
    }
}

fun scheduleLoadMoviesWorker(context: Context) {
    logd("Scheduling periodic load movies worker")
    val workRequest = PeriodicWorkRequestBuilder<LoadMoviesWorker>(12, TimeUnit.HOURS)
        .setInitialDelay(minutesUntilTomorrowAt8(), TimeUnit.MINUTES)
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
        .addTag(LoadMoviesWorker.TAG)
        .build()
    WorkManager
        .getInstance(context)
        .enqueueUniquePeriodicWork(
            LoadMoviesWorker.TAG,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
}
