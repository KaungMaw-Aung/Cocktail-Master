package com.kaungmaw.cocktailmaster.detail

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaungmaw.cocktailmaster.database.DrinkDatabase
import com.kaungmaw.cocktailmaster.network.DetailDto
import com.kaungmaw.cocktailmaster.repository.CocktailRepository
import kotlinx.coroutines.launch

class DetailViewModel(application: Application, drinkIDKey: String) : ViewModel() {

    private val database = DrinkDatabase.getInMemoryDatabase(application)

    private val repository = CocktailRepository(database)

    private val _responseDetail = MutableLiveData<DetailDto>()
    val responseDetail : LiveData<DetailDto>
        get() = _responseDetail

    init {
        getCocktailById(drinkIDKey)
    }

    private fun getCocktailById(key: String){
        viewModelScope.launch {
            _responseDetail.value = repository.refreshCocktailDetail(key)
        }
    }


}