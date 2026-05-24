package com.example
import android.util.Log

class MyUncaughtExceptionHandler : Thread.UncaughtExceptionHandler {
    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
    override fun uncaughtException(t: Thread, e: Throwable) {
        Log.e("CRITICAL_CRASH", "Uncaught exception", e)
        defaultHandler?.uncaughtException(t, e)
    }
}
