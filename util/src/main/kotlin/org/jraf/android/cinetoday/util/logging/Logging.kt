@file:Suppress("NOTHING_TO_INLINE")

package org.jraf.android.cinetoday.util.logging

import timber.log.Timber

fun initLogging() {
    Timber.plant(Timber.DebugTree())
}

inline fun logd(message: String) {
    Timber.d(message)
}

inline fun logd(any: Any) {
    Timber.d(any.toString())
}

inline fun logd(throwable: Throwable) {
    Timber.d(throwable)
}

inline fun logd(throwable: Throwable, message: String) {
    Timber.d(throwable, message)
}

inline fun logd(throwable: Throwable, any: Any) {
    Timber.d(throwable, any.toString())
}
