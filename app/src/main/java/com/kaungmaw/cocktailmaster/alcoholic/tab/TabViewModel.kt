package com.kaungmaw.cocktailmaster.alcoholic.tab

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaungmaw.cocktailmaster.database.DrinkDatabase
import com.kaungmaw.cocktailmaster.domain.DrinkDomain
import com.kaungmaw.cocktailmaster.repository.CocktailRepository
import kotlinx.coroutines.launch

class TabViewModel(val app: Application): ViewModel() {

    private val repository = CocktailRepository(DrinkDatabase.getDatabase(app))

    private val _alcoholicResponse = MutableLiveData<List<DrinkDomain>>()
    val alcoholicResponse: LiveData<List<DrinkDomain>>
        get() = _alcoholicResponse

    fun filterAlcoholic(type: String){
        getAlcoholicByType(type)
    }

    private fun getAlcoholicByType(type: String){
        viewModelScope.launch {
            _alcoholicResponse.value = repository.getAlcoholicList(type)
        }
    }

}