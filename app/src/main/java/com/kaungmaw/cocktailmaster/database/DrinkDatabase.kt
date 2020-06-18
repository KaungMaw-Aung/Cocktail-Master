package com.kaungmaw.cocktailmaster.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [DrinkEntity::class], version = 1, exportSchema = false)
abstract class DrinkDatabase : RoomDatabase() {

    abstract val drinkDao: DrinkDao

    companion object {
        @Volatile
        private var INSTANCE: DrinkDatabase? = null

        fun getInMemoryDatabase(context: Context): DrinkDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.inMemoryDatabaseBuilder(
                        context.applicationContext,
                        DrinkDatabase::class.java
                    ).fallbackToDestructiveMigration().build()
                }
                return instance
            }
        }
    }
}