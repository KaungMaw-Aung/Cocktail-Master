package com.kaungmaw.cocktailmaster.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DrinkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg drinkEntities: DrinkEntity)

    @Query("SELECT * FROM drink_table WHERE category = :category")
    fun getDrinkEntityByCategory(category: String): LiveData<List<DrinkEntity>>

}