package com.kaungmaw.cocktailmaster.domain

data class DrinkDomain(
    val drinkID: String,
    val drinkName: String,
    val category: String,
    val instructions: String,
    val drinkImg: String,
    val ingredientList: List<String>
)