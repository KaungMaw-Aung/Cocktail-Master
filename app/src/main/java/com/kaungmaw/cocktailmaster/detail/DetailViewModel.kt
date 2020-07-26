package com.kaungmaw.cocktailmaster.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kaungmaw.cocktailmaster.database.DrinkDatabase
import com.kaungmaw.cocktailmaster.repository.CocktailRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.reflect.Field

private const val TAG = "DetailViewModel"

class DetailViewModel(application: Application, private val drinkIDKey: String) : ViewModel() {

    private val database = DrinkDatabase.getInMemoryDatabase(application)

    private val repository = CocktailRepository(database)

    private val keyID = MutableLiveData<String>()

    // Access a Cloud Firestore instance from your Activity
    private val db = Firebase.firestore
    private val docRef =
        db.collection("users").document("${FirebaseAuth.getInstance().currentUser?.uid}")


    val responseDetail = keyID.distinctUntilChanged().switchMap {
        repository.getDetailFromDatabase(it)
    }

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean>
        get() = _isFavorite

    init {
        getCocktailById(drinkIDKey)
        _isFavorite.value
    }

    private fun getCocktailById(key: String) {
        viewModelScope.launch {
            repository.refreshCocktailDetail(key)
            keyID.value = key
        }
    }

    fun assignFavorite() {
        var isContainInList = false
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                isContainInList = if (document.data?.get("favorite_list") == null) {
                    false
                } else {
                    document.data?.get("favorite_list").let {
                        val list = it as ArrayList<*>
                        list.contains(drinkIDKey)
                    }
                }
//                isContainInList = document.data?.get("favorite_list")?.let {
//                    val list = it as ArrayList<*>
//                    list.contains(drinkIDKey)
//                }
                when (isContainInList) {
                    true -> {
                        _isFavorite.value = false
                        docRef.update("favorite_list", FieldValue.arrayRemove(drinkIDKey))
                    }
                    else -> {
                        _isFavorite.value = true
                        docRef.update("favorite_list", FieldValue.arrayUnion(drinkIDKey))
                    }
                }
            }
        }
    }


    fun toggleFavorite() {
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val isFavorite = document.data?.get("favorite_list")?.let {
                        val list = it as ArrayList<*>
                        list.contains(drinkIDKey)
                    }
                    _isFavorite.value = isFavorite
                }
            }
    }


}



