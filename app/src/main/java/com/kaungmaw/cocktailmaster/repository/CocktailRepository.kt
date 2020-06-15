package com.kaungmaw.cocktailmaster.repository

import com.kaungmaw.cocktailmaster.network.CocktailApi
import com.kaungmaw.cocktailmaster.network.OverviewDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CocktailRepository {

    suspend fun refreshCocktailList(category: String): OverviewDto{
        return withContext(Dispatchers.IO){
             CocktailApi.retrofitService.getCocktailByCategory(category).await()
        }
    }

}