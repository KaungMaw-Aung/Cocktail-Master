package com.kaungmaw.cocktailmaster.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/"

val moshi: Moshi = Moshi.Builder().build()

val retrofit: Retrofit =
    Retrofit.Builder().addCallAdapterFactory(CoroutineCallAdapterFactory()).addConverterFactory(
        MoshiConverterFactory.create(moshi)
    ).baseUrl(BASE_URL).build()

interface CocktailApiService {

    @GET("filter.php")
    fun getCocktailByCategoryAsync(@Query(value = "c") category: String): Deferred<OverviewDto>

    @GET("lookup.php")
    fun getDetailByIdAsync(@Query(value = "i") keyID: String): Deferred<DetailDto>

    @GET("filter.php")
    fun getAlcoholicAsync(@Query(value = "a") type: String): Deferred<OverviewDto>

}

object CocktailApi {
    val retrofitService: CocktailApiService by lazy {
        retrofit.create(CocktailApiService::class.java)
    }
}