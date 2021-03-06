package com.kaungmaw.cocktailmaster.database

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface DrinkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg drinkEntities: DrinkEntity)

    @Query("SELECT * FROM drink_table WHERE category = :category ORDER BY drinkID DESC")
    fun getDrinkEntityByCategory(category: String): LiveData<List<DrinkEntity>>

//    @Update(onConflict = OnConflictStrategy.REPLACE)
//    fun updateDetail(drinkEntity: DrinkEntity)

    @Query("SELECT * FROM drink_table WHERE drinkID = :keyID")
    fun getDetailByID(keyID: String): LiveData<DrinkEntity>

    @Query("SELECT * FROM drink_table WHERE drinkID = :keyID")
    suspend fun getFavoriteByID(keyID: String): DrinkEntity

}