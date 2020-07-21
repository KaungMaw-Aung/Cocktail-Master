package com.kaungmaw.cocktailmaster.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kaungmaw.cocktailmaster.database.DrinkDatabase
import com.kaungmaw.cocktailmaster.repository.CocktailRepository
import kotlinx.coroutines.launch

private const val TAG = "DetailViewModel"

class DetailViewModel(application: Application, private val drinkIDKey: String) : ViewModel() {

    private val database = DrinkDatabase.getInMemoryDatabase(application)

    private val repository = CocktailRepository(database)

    private val keyID = MutableLiveData<String>()

    // Access a Cloud Firestore instance from your Activity
    private val db = Firebase.firestore
    private val docRef = db.collection("favorite_drinks").document(drinkIDKey)
//    var isFavorite: Any? = null

    private val _isFavorite = MutableLiveData<Any>()
    val isFavorite : LiveData<Any>
        get() = _isFavorite


    val responseDetail = keyID.distinctUntilChanged().switchMap {
        repository.getDetailFromDatabase(it)
    }

    init {
        getCocktailById(drinkIDKey)
        getIsFavorite()
    }

    private fun getCocktailById(key: String) {
        viewModelScope.launch {
            repository.refreshCocktailDetail(key)
            keyID.value = key
        }
    }

    private fun getIsFavorite() {
        docRef.get()
            .addOnSuccessListener { document ->
                _isFavorite.value = if (document != null) {
                    document.data?.get("isFavorite")
                } else {
                    null
                }
            }
            .addOnFailureListener { exception ->
                Log.i(TAG, "get failed with ", exception)
                _isFavorite.value = null
            }
    }

    fun toggleFavorite() {
        getIsFavorite()
        if (_isFavorite.value == true) {
            db.collection("favorite_drinks").document(drinkIDKey)
                .delete()
                .addOnSuccessListener { Log.i(TAG, "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.i(TAG, "Error deleting document", e) }
        } else {
            val favorite = hashMapOf(
                "isFavorite" to true
            )

//         Add a new document with a generated ID
            docRef
                .set(favorite)
                .addOnSuccessListener { _ ->
                    Log.i(TAG, "Succeed!")
                }
                .addOnFailureListener { e ->
                    Log.i(TAG, "Failed! ${e.message}")
                }
        }
    }
}


