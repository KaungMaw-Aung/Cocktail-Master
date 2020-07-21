package com.kaungmaw.cocktailmaster

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kaungmaw.cocktailmaster.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.drawer_header.view.*
import java.io.ByteArrayOutputStream

private const val RC_SIGN_IN = 101

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Choose authentication providers
    private val providers = arrayListOf(
        AuthUI.IdpConfig.PhoneBuilder().build()
//            AuthUI.IdpConfig.EmailBuilder().build(),
//            AuthUI.IdpConfig.GoogleBuilder().build(),
//            AuthUI.IdpConfig.FacebookBuilder().build(),
//            AuthUI.IdpConfig.TwitterBuilder().build()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        //setup navigationUi with toolbar
        val navController = this.findNavController(R.id.nav_host_fragment)
        val toolbar = binding.toolbar
        NavigationUI.setupWithNavController(toolbar, navController, binding.drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

        //toggle for dark mode
        val switch = binding.switch1
        switch.isChecked = DarkModeHelper.getInstance(applicationContext).isDark()
        switch.setOnClickListener {
            DarkModeHelper.getInstance(applicationContext).toggleDark()
        }


        // Create and launch sign-in intent
        if (isNotLoggedIn()) {
            startLoginActivity(providers)
        } else {
            bindUserData()
        }

/*        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("MainActivity", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                task.result?.apply {
                    Log.i("MainActivity", "token : ${this.token}")
                }

            }
            )

        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 5
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(mapOf("darkThemeSupport" to false))

        switch.isVisible = remoteConfig.getBoolean("darkThemeSupport")

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    switch.isVisible = remoteConfig.getBoolean("darkThemeSupport")
                    Toast.makeText(
                        this, "Fetch and activate succeeded $updated",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this, "Fetch failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }*/

    }

    private fun bindUserData() {
        val navView = binding.navView
        val headerView = navView.getHeaderView(0)
        headerView.tv_username.text = FirebaseAuth.getInstance().currentUser?.phoneNumber
        headerView.iv_profile.setOnClickListener {
            selectImage(this)
        }
        headerView.btn_logout.setOnClickListener {
            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    Log.i("MainActivity", "Logout Complete!")
                    startLoginActivity(providers)
                }
        }
        Log.i(
            "MainActivity",
            "Already Login! ${FirebaseAuth.getInstance().currentUser?.phoneNumber}"
        )
    }

    private fun startLoginActivity(providers: ArrayList<AuthUI.IdpConfig>) {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
//            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                bindUserData()
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                Log.d("MainActivity", "Sign in failed!")
            }
        }

        if (resultCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                0 -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage = data.extras?.get("data") as Bitmap
                    uploadToStorage(selectedImage)
                }
                1 -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImgUri = data.data
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImgUri!!)
                    uploadToStorage(bitmap)
                }
            }
        }
    }

    private fun isNotLoggedIn() = FirebaseAuth.getInstance().currentUser == null

    private fun selectImage(context: Context) {
        val listItems = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle("Choose Action")
        dialog.setItems(listItems) { dialogInt, item ->
            when (listItems[item]) {
                "Take Photo" -> {
                    val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, 0)
                }
                "Choose from Gallery" -> {
                    val pickPhotoIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    startActivityForResult(pickPhotoIntent, 1)
                }
                else -> dialogInt.dismiss()
            }
        }
        dialog.show()
    }

    private fun uploadToStorage(selectedImage: Bitmap) {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val imgRef = storageRef.child("profiles")
        val avatarRef = imgRef.child("${FirebaseAuth.getInstance().currentUser?.uid}.jpg")

        val baos = ByteArrayOutputStream()
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val dataArray = baos.toByteArray()

        val uploadTask = avatarRef.putBytes(dataArray)
        uploadTask.addOnFailureListener {
            Log.i("MainActivity", "error : ${it.message}")
        }.addOnSuccessListener {
            Log.i("MainActivity", "Upload uri : ${it.uploadSessionUri}")
            avatarRef.downloadUrl.addOnSuccessListener { uri ->
                Log.i("MainActivity", "Download uri : $uri")
            }.addOnFailureListener { e ->
                Log.i("MainActivity", "error : ${e.message}")
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(this.findNavController(R.id.nav_host_fragment), binding.drawerLayout)
    }
}
