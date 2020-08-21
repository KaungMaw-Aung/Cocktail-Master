package com.kaungmaw.cocktailmaster

import android.app.Application
import timber.log.Timber

class CockTailApp: Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree)
        DarkModeHelper.getInstance(this)
    }

}