package com.kaungmaw.cocktailmaster.alcoholic.tab

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TabViewModelFactory(private val app: Application): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TabViewModel::class.java)) {
            return TabViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}