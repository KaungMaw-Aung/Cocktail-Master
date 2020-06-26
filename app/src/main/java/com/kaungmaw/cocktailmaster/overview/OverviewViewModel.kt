package com.kaungmaw.cocktailmaster.overview

import android.app.Application
import androidx.lifecycle.*
import com.kaungmaw.cocktailmaster.database.DrinkDatabase
import com.kaungmaw.cocktailmaster.repository.CocktailRepository
import kotlinx.coroutines.launch

class OverviewViewModel(application: Application) : ViewModel() {

    private val database = DrinkDatabase.getInMemoryDatabase(application)

    private val repository = CocktailRepository(database)

    private val categoryLive = MutableLiveData<String>()

    //list to observe
    val drinkListResult = categoryLive
        .distinctUntilChanged()
        .switchMap { repository.getDrinkFromDatabase(it) }

    //list for chips
    val listForChips = listOf(
        "Cocktail",
        "Ordinary Drink",
        "Milk / Float / Shake",
        "Other/Unknown",
        "Cocoa",
        "Shot",
        "Coffee / Tea",
        "Homemade Liqueur",
        "Punch / Party Drink",
        "Beer",
        "Soft Drink / Soda"
    )


    //previous chip
    var savedChip: String = "Cocktail"

    init {
        getDrinkByCategory("Cocktail")
    }

    fun filterDrink(category: String, isChecked: Boolean) {
        if (isChecked && savedChip != category) {
            getDrinkByCategory(category)
        }
    }

    private fun getDrinkByCategory(category: String) {
        viewModelScope.launch {
           repository.refreshCocktailList(category)
            categoryLive.value = category
        }

    }

}