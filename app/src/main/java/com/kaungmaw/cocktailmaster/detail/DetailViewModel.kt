package com.kaungmaw.cocktailmaster.detail

import android.app.Application
import androidx.lifecycle.*
import com.kaungmaw.cocktailmaster.database.DrinkDatabase
import com.kaungmaw.cocktailmaster.repository.CocktailRepository
import kotlinx.coroutines.launch

class DetailViewModel(application: Application, drinkIDKey: String) : ViewModel() {

    private val database = DrinkDatabase.getInMemoryDatabase(application)

    private val repository = CocktailRepository(database)

    private val keyID = MutableLiveData<String>()

    val responseDetail = keyID.distinctUntilChanged().switchMap {
        repository.getDetailFromDatabase(it)
    }

    init {
        getCocktailById(drinkIDKey)
    }

    private fun getCocktailById(key: String) {
        viewModelScope.launch {
            repository.refreshCocktailDetail(key)
            keyID.value = key
        }
    }

}