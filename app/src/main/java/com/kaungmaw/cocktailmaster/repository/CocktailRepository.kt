package com.kaungmaw.cocktailmaster.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.kaungmaw.cocktailmaster.database.DrinkDatabase
import com.kaungmaw.cocktailmaster.database.asDomainModel
import com.kaungmaw.cocktailmaster.domain.DrinkDomain
import com.kaungmaw.cocktailmaster.network.CocktailApi
import com.kaungmaw.cocktailmaster.network.DetailDto
import com.kaungmaw.cocktailmaster.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CocktailRepository(private val database: DrinkDatabase) {

    fun getDrinkFromDatabase(category: String): LiveData<List<DrinkDomain>> {
        return Transformations.map(database.drinkDao.getDrinkEntityByCategory(category)) {
            it.asDomainModel()
        }
    }

    suspend fun refreshCocktailList(category: String) {
        try {
            val response = withContext(Dispatchers.IO) {
                CocktailApi.retrofitService.getCocktailByCategoryAsync(category).await()
            }
            database.drinkDao.insertAll(*response.asDatabaseModel(category))
//            response.asDatabaseModel().map {
//                it.copy(category = category)
//            }.also {
//                database.drinkDao.insertAll(*it.toTypedArray())
//            }
        } catch (e: Exception) {
            Log.e("CocktailRepository", e.message!!)
        }
    }

    suspend fun refreshCocktailDetail(keyID: String): DetailDto{
        return withContext(Dispatchers.IO) {
            CocktailApi.retrofitService.getDetailByIdAsync(keyID).await()
        }
    }
}