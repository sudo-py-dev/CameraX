package com.camerax.presentation.ui.crash

import android.content.Context
import android.content.Intent
import android.os.Process
import android.util.Log
import kotlin.system.exitProcess

class CrashHandler(private val context: Context) : Thread.UncaughtExceptionHandler {
    companion object {
        private const val TAG = "CrashHandler"
        const val EXTRA_CRASH_MESSAGE = "crash_message"
        const val EXTRA_CRASH_TRACE = "crash_trace"
    }

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    fun install() {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(
        thread: Thread,
        throwable: Throwable,
    ) {
        try {
            Log.e(TAG, "Uncaught exception in ${thread.name}", throwable)
            val intent =
                Intent(context, CrashActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra(EXTRA_CRASH_MESSAGE, throwable.localizedMessage.orEmpty())
                    putExtra(EXTRA_CRASH_TRACE, throwable.stackTraceToString().take(2000))
                }
            context.startActivity(intent)
        } catch (e: Exception) {
            defaultHandler?.uncaughtException(thread, throwable)
        } finally {
            Process.killProcess(Process.myPid())
            exitProcess(1)
        }
    }
}
