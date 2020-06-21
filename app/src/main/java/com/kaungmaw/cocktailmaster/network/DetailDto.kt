package com.kaungmaw.cocktailmaster.network

import com.kaungmaw.cocktailmaster.database.DrinkEntity
import com.kaungmaw.cocktailmaster.database.RoomConverter
import com.kaungmaw.cocktailmaster.domain.DrinkDomain
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DetailDto(
    @Json(name = "drinks") val drink: List<Map<String, String?>>
)

fun DetailDto.asDomainModel(): DrinkDomain {
    val map = drink[0]
    return DrinkDomain(
        drinkID = map["idDrink"]!!,
        drinkName = map["strDrink"]!!,
        category = map["strCategory"]!!,
        instructions = map["strInstructions"]!!,
        drinkImg = map["strDrinkThumb"] ?: "",
        ingredientList = map.keys.filter {
            it.contains("strIngredient")
        }.mapNotNull {
            map[it]
        }
    )
}

fun DetailDto.asDatabaseModel(): DrinkEntity {
    val map = drink[0]
    return DrinkEntity(
        drinkID = map["idDrink"]!!,
        drinkName = map["strDrink"]!!,
        category = map["strCategory"]!!,
        instructions = map["strInstructions"]!!,
        drinkImg = map["strDrinkThumb"] ?: "",
        ingredients = RoomConverter.adapter.toJson(
            map.keys.filter {
                it.contains("strIngredient")
            }.filter {
                map[it] != ""
            }.mapNotNull {
                map[it]
            }
        )
    )
}

