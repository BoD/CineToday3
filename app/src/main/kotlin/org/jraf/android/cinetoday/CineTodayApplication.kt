package org.jraf.android.cinetoday

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.jraf.android.cinetoday.util.logging.initLogging

@HiltAndroidApp
class CineTodayApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initLogging()
    }
}
