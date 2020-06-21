package com.kaungmaw.cocktailmaster.database

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class RoomConverter {
    companion object{
        private val moshi = Moshi.Builder().build()
        private val type = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter = moshi.adapter<List<String>>(type)
    }
}