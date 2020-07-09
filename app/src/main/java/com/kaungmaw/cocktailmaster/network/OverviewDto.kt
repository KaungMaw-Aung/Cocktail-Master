package com.kaungmaw.cocktailmaster.network

import com.kaungmaw.cocktailmaster.database.DrinkEntity
import com.kaungmaw.cocktailmaster.database.RoomConverter
import com.kaungmaw.cocktailmaster.domain.DrinkDomain
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

fun OverviewDto.asDatabaseModel(cat: String): Array<DrinkEntity>{
    return drinksList.map {
        DrinkEntity(
            drinkID = it.drinkID,
            drinkName = it.drinkName,
            category = cat,
            instructions = "",
            drinkImg = it.drinkImgUrl,
            ingredients = RoomConverter.adapter.toJson(emptyList())
        )
    }.toTypedArray()
}

fun OverviewDto.asDomainModel(): List<DrinkDomain>{
    return drinksList.map {
        DrinkDomain(
            drinkID = it.drinkID,
            drinkName = it.drinkName,
            category = "",
            instructions = "",
            drinkImg = it.drinkImgUrl,
            ingredientList = emptyList()
        )
    }
}
