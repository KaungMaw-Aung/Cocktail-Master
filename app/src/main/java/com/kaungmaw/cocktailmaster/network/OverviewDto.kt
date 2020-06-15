package com.kaungmaw.cocktailmaster.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OverviewDto (
    @Json(name = "drinks") val drinksList: List<Drink>
)

@JsonClass(generateAdapter = true)
data class Drink (
    @Json(name = "idDrink") val drinkID: String,
    @Json(name = "strDrink") val drinkName: String,
    @Json(name = "strDrinkThumb") val drinkImgUrl: String
)
