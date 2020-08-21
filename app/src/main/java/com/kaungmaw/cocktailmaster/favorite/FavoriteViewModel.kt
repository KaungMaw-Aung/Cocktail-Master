package com.kaungmaw.cocktailmaster.favorite

import android.app.Application
import androidx.lifecycle.ViewModel
import com.kaungmaw.cocktailmaster.database.DrinkDatabase
import com.kaungmaw.cocktailmaster.repository.CocktailRepository

class FavoriteViewModel(application: Application): ViewModel() {
    private val database = DrinkDatabase.getDatabase(application)
    private val repository = CocktailRepository(database)

    val favoriteList = repository.getFavoriteList()

}