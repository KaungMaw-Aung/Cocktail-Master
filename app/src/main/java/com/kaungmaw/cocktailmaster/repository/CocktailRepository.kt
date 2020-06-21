package com.kaungmaw.cocktailmaster.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.kaungmaw.cocktailmaster.database.DrinkDatabase
import com.kaungmaw.cocktailmaster.database.asDomainDetailModel
import com.kaungmaw.cocktailmaster.database.asDomainModel
import com.kaungmaw.cocktailmaster.domain.DrinkDomain
import com.kaungmaw.cocktailmaster.network.CocktailApi
import com.kaungmaw.cocktailmaster.network.DetailDto
import com.kaungmaw.cocktailmaster.network.asDatabaseModel
import com.kaungmaw.cocktailmaster.network.asDomainModel
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
        } catch (e: Exception) {
            Log.e("CocktailRepository", e.message!!)
        }
    }

    fun getDetailFromDatabase(keyID: String): LiveData<DrinkDomain> {
        return Transformations.map(database.drinkDao.getDetailByID(keyID)) {
            asDomainDetailModel(it)
        }
    }

    suspend fun refreshCocktailDetail(keyID: String){
         try {
             val response = withContext(Dispatchers.IO) {
                 CocktailApi.retrofitService.getDetailByIdAsync(keyID).await().asDatabaseModel()
             }
             database.drinkDao.insertAll(response)
         }catch (e: Exception){
             Log.e("CocktailRepository", e.message!!)
         }
    }
}