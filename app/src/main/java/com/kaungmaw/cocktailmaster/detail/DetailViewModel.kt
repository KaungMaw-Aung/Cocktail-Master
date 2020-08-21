package com.kaungmaw.cocktailmaster.detail

import android.app.Application
import androidx.lifecycle.*
import com.kaungmaw.cocktailmaster.database.DrinkDatabase
import com.kaungmaw.cocktailmaster.repository.CocktailRepository
import kotlinx.coroutines.launch

private const val TAG = "DetailViewModel"

class DetailViewModel(application: Application, private val drinkIDKey: String) : ViewModel() {

    private val database = DrinkDatabase.getDatabase(application)

    private val repository = CocktailRepository(database)

    private val keyID = MutableLiveData<String>()

    val responseDetail =
        keyID.distinctUntilChanged().switchMap { repository.getDetailFromDatabase(it) }

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean>
        get() = _isFavorite

    private val _firstFavCheck = MutableLiveData<Boolean>()
    val firstFavCheck: LiveData<Boolean>
        get() = _firstFavCheck

    init {
        getCocktailById(drinkIDKey)
        repository.isAlreadyInFavoriteList(_firstFavCheck,drinkIDKey)
    }

    fun switchFavorite(){
        repository.toggleFavorite(_isFavorite,drinkIDKey)
    }

    private fun getCocktailById(key: String) {
        viewModelScope.launch {
            repository.refreshCocktailDetail(key)
            keyID.value = key
        }
    }
}



