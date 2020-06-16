package com.kaungmaw.cocktailmaster.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.chip.Chip
import com.kaungmaw.cocktailmaster.network.OverviewDto
import com.kaungmaw.cocktailmaster.repository.CocktailRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OverviewViewModel : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(job + Dispatchers.Main)
    private val repository = CocktailRepository()

    //list to observe
    private val _drinkListResult = MutableLiveData<OverviewDto>()
    val drinkListResult: LiveData<OverviewDto>
        get() = _drinkListResult

    //list for chips
    private val _listForChips = MutableLiveData<List<String>>()
    val listForChips: LiveData<List<String>>
        get() = _listForChips

    //previous chip
    val savedChip = MutableLiveData<Chip>()



    init {
        getDrinkByCategory("Cocktail")
        _listForChips.value = listOf(
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
    }

    fun filterDrink(category: String , isChecked: Boolean){
        if(isChecked){
            getDrinkByCategory(category)
        }
    }

    private fun getDrinkByCategory(category: String) {
        scope.launch {
            _drinkListResult.value = repository.refreshCocktailList(category)
        }
    }


    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

}