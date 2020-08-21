package com.kaungmaw.cocktailmaster.profile

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kaungmaw.cocktailmaster.database.DrinkDatabase
import com.kaungmaw.cocktailmaster.repository.CocktailRepository
import com.kaungmaw.cocktailmaster.repository.UserProfile
import java.io.File

class ProfileViewModel(val application: Application) : ViewModel() {

    private val repository = CocktailRepository(DrinkDatabase.getDatabase(application))

    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile : LiveData<UserProfile>
        get() = _userProfile

    fun getUserData() {
        repository.getUserData(_userProfile)
    }

    fun uploadToStorage(file: File) {
        repository.uploadToStorage(file)
    }

    fun saveUserToFireStore(){
        repository.saveLoginUserToFireStore()
    }
}

