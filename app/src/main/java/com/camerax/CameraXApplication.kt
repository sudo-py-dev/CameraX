package com.camerax

import android.app.Application
import com.camerax.di.AppContainer
import com.camerax.di.AppContainerImpl
import com.camerax.presentation.ui.crash.CrashHandler

class CameraXApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
        CrashHandler(this).install()
    }
}
