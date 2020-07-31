package com.kaungmaw.cocktailmaster.profile

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

private const val TAG = "ProfileViewModel"

class ProfileViewModel : ViewModel() {

    fun getUserData() {
        FirebaseAuth.getInstance().currentUser?.uid?.let {
            Firebase.firestore.collection("users").document(it).addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.i(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.i(TAG, "Current data: ${snapshot.data}")
                    _profilePhoto.value = snapshot.data?.get("profile_url").toString()
                    _displayName.value = snapshot.data?.get("phone_no") ?: snapshot.data?.get("display_name")
                    _email.value = snapshot.data?.get("email")
                } else {
                    Log.i(TAG, "Current data: null")
                }
            }
        }
    }

    private val _profilePhoto = MutableLiveData<String>()
    val profilePhoto: LiveData<String>
        get() = _profilePhoto

    private val _displayName = MutableLiveData<Any>()
    val displayName: LiveData<Any>
        get() = _displayName

    private val _email = MutableLiveData<Any?>()
    val email: LiveData<Any?>
        get() = _email

    fun uploadToStorage(selectedImage: Bitmap, docRef : CollectionReference) {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val imgRef = storageRef.child("profiles")
        val avatarRef = imgRef.child("${FirebaseAuth.getInstance().currentUser?.uid}.jpg")

        val baos = ByteArrayOutputStream()
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val dataArray = baos.toByteArray()

        val uploadTask = avatarRef.putBytes(dataArray)
        uploadTask.addOnFailureListener {
            Log.i(TAG, "error : ${it.message}")
        }
            .addOnSuccessListener {
                avatarRef.downloadUrl.addOnSuccessListener { uri ->
                    Log.i(TAG, "Download uri : $uri")
                    val updatedPhotoUri = hashMapOf("profile_url" to uri.toString())
                    docRef.document("${FirebaseAuth.getInstance().currentUser?.uid}")
                        .set(updatedPhotoUri, SetOptions.merge())
                }.addOnFailureListener { e ->
                    Log.i(TAG, "error : ${e.message}")
                }
            }
    }


}