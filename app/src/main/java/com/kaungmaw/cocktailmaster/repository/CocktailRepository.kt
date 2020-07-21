package com.kaungmaw.cocktailmaster.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kaungmaw.cocktailmaster.database.DrinkDatabase
import com.kaungmaw.cocktailmaster.database.asDomainDetailModel
import com.kaungmaw.cocktailmaster.database.asDomainModel
import com.kaungmaw.cocktailmaster.domain.DrinkDomain
import com.kaungmaw.cocktailmaster.network.CocktailApi
import com.kaungmaw.cocktailmaster.network.asDatabaseModel
import com.kaungmaw.cocktailmaster.network.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "CocktailRepository"

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

    suspend fun refreshCocktailDetail(keyID: String) {
        try {
            val response = withContext(Dispatchers.IO) {
                CocktailApi.retrofitService.getDetailByIdAsync(keyID).await().asDatabaseModel()
            }
            database.drinkDao.insertAll(response)
        } catch (e: Exception) {
            Log.e("CocktailRepository", e.message!!)
        }
    }

    suspend fun refreshAlcoholic(type: String): List<DrinkDomain> {
        var result = emptyList<DrinkDomain>()
        try {
            withContext(Dispatchers.IO) {
                result = CocktailApi.retrofitService.getAlcoholicAsync(type).await().asDomainModel()
            }
        } catch (e: Exception) {
            Log.e("CocktailRepository", e.message!!)
            result = emptyList()
        }
        return result
    }

    fun getFavoriteList(): MutableLiveData<List<DrinkDomain>> {

        val db = Firebase.firestore
        val favoriteListLive = MutableLiveData<List<DrinkDomain>>()

        db.collection("favorite_drinks")
            .get()
            .addOnSuccessListener { result ->
                GlobalScope.launch {
                    result.map {
                        database.drinkDao.getFavoriteByID(it.id).let { entity ->
                            asDomainDetailModel(entity)
                        }
                    }.also {
                        favoriteListLive.postValue(it)
                    }
                }

//                favoriteList.map { id ->
//                    Transformations.map(database.drinkDao.getDetailByID(id)) {
//                        asDomainDetailModel(it)
//                    }
//                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
        return favoriteListLive
    }
}