package com.kaungmaw.cocktailmaster.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kaungmaw.cocktailmaster.database.DrinkDatabase
import com.kaungmaw.cocktailmaster.database.asDomainDetailModel
import com.kaungmaw.cocktailmaster.database.asDomainModel
import com.kaungmaw.cocktailmaster.domain.DrinkDomain
import com.kaungmaw.cocktailmaster.network.CocktailApi
import com.kaungmaw.cocktailmaster.network.asDatabaseModel
import com.kaungmaw.cocktailmaster.network.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileInputStream

class CocktailRepository(private val database: DrinkDatabase) {

    private val db = Firebase.firestore
    private val collectionRef = db.collection("users")
    private val currentUserRef = collectionRef.document("${FirebaseAuth.getInstance()
        .currentUser?.uid}")

    fun getDrinkFromDatabase(category: String): LiveData<List<DrinkDomain>> = database.drinkDao
        .getDrinkEntityByCategory(category).map { it.asDomainModel() }

    suspend fun refreshCocktailList(category: String) {
        try {
           withContext(Dispatchers.IO) {
                CocktailApi.retrofitService.getCocktailByCategoryAsync(category).await()
            }.also { database.drinkDao.insertAll(*it.asDatabaseModel(category))}
        } catch (e: Exception) {
            Timber.e(e.message!!)
        }
    }


    fun getDetailFromDatabase(keyID: String): LiveData<DrinkDomain> = database.drinkDao
        .getDetailByID(keyID).map { asDomainDetailModel(it) }

    suspend fun refreshCocktailDetail(keyID: String) {
        try {
            withContext(Dispatchers.IO) {
                CocktailApi.retrofitService.getDetailByIdAsync(keyID).await().asDatabaseModel()
            }.also { database.drinkDao.updateDetail(it) }
        } catch (e: Exception) {
            Timber.e(e.message!!)
        }
    }

    fun isAlreadyInFavoriteList(favLive: MutableLiveData<Boolean>, drinkIDKey: String) {
        currentUserRef.get().addOnSuccessListener { document ->
            val list = document.data?.get("favorite_list")?.let { it as ArrayList<String>}
            favLive.value = !(list == null || list.contains(drinkIDKey).not())
        }
    }

    private fun isAlreadyInFavoriteList(documentSnapshot: DocumentSnapshot, drinkIDKey: String): Boolean {
        val list = documentSnapshot.data?.get("favorite_list")?.let { it as ArrayList<String> }
        return !(list == null || list.contains(drinkIDKey).not())
    }

    fun toggleFavorite(isFavorite: MutableLiveData<Boolean> , drinkIDKey: String) {
        currentUserRef.get().addOnSuccessListener { document ->

            when (isAlreadyInFavoriteList(document, drinkIDKey)) {
                true -> {
                    isFavorite.value = false
                    currentUserRef.update("favorite_list", FieldValue.arrayRemove(drinkIDKey))
                }
                else -> {
                    isFavorite.value = true
                    currentUserRef.update("favorite_list", FieldValue.arrayUnion(drinkIDKey))
                }
            }
        }.addOnFailureListener { e ->
            Timber.e(e.message!!)
        }
    }


    suspend fun getAlcoholicList(type: String): List<DrinkDomain> {
        var result = emptyList<DrinkDomain>()
        try {
            withContext(Dispatchers.IO) {
                result = CocktailApi.retrofitService.getAlcoholicAsync(type).await().asDomainModel()
            }
        } catch (e: Exception) {
            Timber.e(e.message!!)
            result = emptyList()
        }
        return result
    }


    fun getFavoriteList(): MutableLiveData<List<DrinkDomain>?> {
        val favoriteListLive = MutableLiveData<List<DrinkDomain>?>()

        currentUserRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    GlobalScope.launch {
                        val list = snapshot.data?.get("favorite_list")
                        if (list == null) {
                            favoriteListLive.postValue(null)
                        } else {
                            val arrayList = list as ArrayList<String>
                            if (arrayList.isEmpty()) { favoriteListLive.postValue(null) } else {
                                list.map {
                                    database.drinkDao.getFavoriteByID(it).let { entity ->
                                        asDomainDetailModel(entity)
                                    }
                                }.also { favoriteListLive.postValue(it) }
                            }
                        }
                    }
                }
            }
        return favoriteListLive
    }


    fun uploadToStorage(selectedImageFile: File) {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val imgRef = storageRef.child("profiles")
        val avatarRef = imgRef.child("${FirebaseAuth.getInstance().currentUser?.uid}.jpg")

        val stream = FileInputStream(selectedImageFile)

        val uploadTask = avatarRef.putStream(stream)
        uploadTask.addOnFailureListener {
            Timber.d("error : ${it.message}")
        }
            .addOnSuccessListener {
                avatarRef.downloadUrl.addOnSuccessListener { uri ->
                    Timber.i("Download uri : $uri")
                    val updatedPhotoUri = hashMapOf("profile_url" to uri.toString())
                    currentUserRef.set(updatedPhotoUri, SetOptions.merge())
                }.addOnFailureListener { e ->
                    Timber.d("error : ${e.message}")
                }
            }
    }

    fun saveLoginUserToFireStore(){
        currentUserRef.get().addOnCompleteListener { document ->
            if (document.result.exists().not()) {
                // Successfully signed in
                val user = hashMapOf(
                    "display_name" to FirebaseAuth.getInstance().currentUser?.displayName,
                    "email" to FirebaseAuth.getInstance().currentUser?.email,
                    "phone_no" to FirebaseAuth.getInstance().currentUser?.phoneNumber,
                    "profile_url" to (FirebaseAuth.getInstance().currentUser?.photoUrl
                        ?: "https://bit.ly/39GLBSV")
                )

                currentUserRef.set(user).addOnSuccessListener {
                    Timber.i("Succeed!")
                }.addOnFailureListener { e ->
                    Timber.d("Failed! ${e.message}")
                }
            }
        }
    }

    fun getUserData(userProfile: MutableLiveData<UserProfile>) {
        FirebaseAuth.getInstance().currentUser?.uid?.let {
            collectionRef.document(it).addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.d("Listen failed: ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    userProfile.value = UserProfile(
                        snapshot.data?.get("phone_no")?.toString() ?: snapshot.data?.get("display_name").toString(),
                        snapshot.data?.get("email")?.toString(),
                        snapshot.data?.get("profile_url").toString()
                    )
                } else {
                    Timber.d("Current data: null")
                }
            }
        }
    }

}

data class UserProfile(
    val name: String,
    val email: String?,
    val photoUrl: String
)