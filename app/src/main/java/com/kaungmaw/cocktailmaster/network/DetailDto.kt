package com.kaungmaw.cocktailmaster.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DetailDto(
    @Json(name = "drinks") val drink: List<DrinkDetailDto>
)

@JsonClass(generateAdapter = true)
class DrinkDetailDto(
    val detailMap: Map<String,String?>
)


//@Json(name = "idDrink")
//val drinkID: String,
//@Json(name = "strDrink")
//val drinkName: String,
//@Json(name = "strCategory")
//val category: String,
//@Json(name = "strInstructions")
//val instructions: String,
//@Json(name = "strDrinkThumb")
//val drinkImgUrl: String,
//@Json(name = "strIngredient1")
//val ingredient1: String?,
//@Json(name = "strIngredient2")
//val ingredient2: String?,
//@Json(name = "strIngredient3")
//val ingredient3: String?,
//@Json(name = "strIngredient4")
//val ingredient4: String?,
//@Json(name = "strMeasure1")
//val measure1: String?,
//@Json(name = "strMeasure2")
//val measure2: String?,
//@Json(name = "strMeasure3")
//val measure3: String?,
//@Json(name = "strMeasure4")
//val measure4: String?

