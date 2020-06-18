package com.kaungmaw.cocktailmaster.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kaungmaw.cocktailmaster.domain.DrinkDomain

@Entity(tableName = "drink_table")
data class DrinkEntity(
    @PrimaryKey
    val drinkID: String,
    val drinkName: String,
    val category: String,
    val instructions: String,
    val drinkImg: String
    )

fun List<DrinkEntity>.asDomainModel(): List<DrinkDomain>{
    return map {
        DrinkDomain(
            drinkID = it.drinkID,
            drinkName = it.drinkName,
            category = it.category,
            instructions = it.instructions,
            drinkImg = it.drinkImg
        )
    }
}
