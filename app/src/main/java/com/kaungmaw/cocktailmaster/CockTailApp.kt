package com.kaungmaw.cocktailmaster

import android.app.Application

class CockTailApp: Application() {

    override fun onCreate() {
        super.onCreate()
        DarkModeHelper.getInstance(this)
    }

}