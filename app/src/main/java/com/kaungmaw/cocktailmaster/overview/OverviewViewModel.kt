package com.kaungmaw.cocktailmaster.overview

import android.app.Application
import androidx.lifecycle.*
import com.kaungmaw.cocktailmaster.database.DrinkDatabase
import com.kaungmaw.cocktailmaster.repository.CocktailRepository
import kotlinx.coroutines.launch

class OverviewViewModel(application: Application) : ViewModel() {

    private val database = DrinkDatabase.getDatabase(application)

    private val repository = CocktailRepository(database)

    private val categoryLive = MutableLiveData<String>()

    //list to observe
    val drinkListResult = categoryLive.distinctUntilChanged().switchMap {
        repository.getDrinkFromDatabase(it)
    }

    val currentFilter = MutableLiveData<String>()

    init {
        filter("Other/Unknown")
    }

    fun filter(category: String) {
        categoryLive.value = category

        viewModelScope.launch {
            repository.refreshCocktailList(category)
        }
    }

}