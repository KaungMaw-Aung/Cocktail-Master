package com.kaungmaw.cocktailmaster

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.kaungmaw.cocktailmaster.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.drawer_header.view.*
import kotlinx.coroutines.delay
import java.io.ByteArrayOutputStream

private const val RC_SIGN_IN = 101

class MainActivity : AppCompatActivity() {

    private lateinit var drawer: DrawerLayout

    private val storage = Firebase.storage
    private val storageRef = storage.reference

    private val imgRef = storageRef.child("images")
    private val avatarRef = imgRef.child("avatar.jpg")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        val navView = binding.navView
        navView.inflateHeaderView(R.layout.drawer_header)
        val headerView = navView.getHeaderView(0)

        headerView.iv_profile.setOnClickListener {
            Toast.makeText(this, "Upload Photo", Toast.LENGTH_LONG).show()
            selectImage(this)
        }



        headerView.btn_logout.setOnClickListener {
            // Get the data from an ImageView as bytes
            val imageView = headerView.iv_profile
            imageView.isDrawingCacheEnabled = true
            imageView.buildDrawingCache()
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val uploadTask = avatarRef.putBytes(data)
            uploadTask.addOnFailureListener {
                Log.i("MainActivity", "error : ${it.message}")
            }.addOnSuccessListener {
                Log.i("MainActivity", "Photo uri : ${it.uploadSessionUri}")
                avatarRef.downloadUrl.addOnSuccessListener { uri ->
                    Log.i("MainActivity", "Photo uri : $uri")
                }.addOnFailureListener { e ->
                    Log.i("MainActivity", "error : ${e.message}")
                }
            }
        }

        // Choose authentication providers
        val providers = arrayListOf(
//            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build()
//            AuthUI.IdpConfig.GoogleBuilder().build(),
//            AuthUI.IdpConfig.FacebookBuilder().build(),
//            AuthUI.IdpConfig.TwitterBuilder().build()
        )

        // Create and launch sign-in intent
//        if (isLoggedIn()){
//            startActivityForResult(
//                AuthUI.getInstance()
//                    .createSignInIntentBuilder()
//                    .setAvailableProviders(providers)
//                    .build(),
//                RC_SIGN_IN
//            )
//        }else{
//            headerView.tv_username.text = FirebaseAuth.getInstance().currentUser?.phoneNumber
//            headerView.btn_logout.setOnClickListener {
//                AuthUI.getInstance()
//                    .signOut(this)
//                    .addOnCompleteListener {
//                        Log.i("MainActivity", "Logout Complete!")
//                    }
//            }
//            Log.i("MainActivity", "Already Login! ${FirebaseAuth.getInstance().currentUser?.phoneNumber}")
//        }

        val navController = this.findNavController(R.id.nav_host_fragment)
        val toolbar = binding.toolbar
        drawer = binding.drawerLayout

        NavigationUI.setupWithNavController(toolbar, navController, drawer)
        NavigationUI.setupWithNavController(binding.navView, navController)


        val switch = binding.switch1
        switch.isChecked = DarkModeHelper.getInstance(applicationContext).isDark()
        switch.setOnClickListener {
            DarkModeHelper.getInstance(applicationContext).toggleDark()
        }

//        FirebaseInstanceId.getInstance().instanceId
//            .addOnCompleteListener(OnCompleteListener { task ->
//                if (!task.isSuccessful) {
//                    Log.w("MainActivity", "getInstanceId failed", task.exception)
//                    return@OnCompleteListener
//                }
//                task.result?.apply {
//                    Log.i("MainActivity", "token : ${this.token}")
//                }
//
//            }
//            )

//        val remoteConfig = Firebase.remoteConfig
//        val configSettings = remoteConfigSettings {
//            minimumFetchIntervalInSeconds = 5
//        }
//        remoteConfig.setConfigSettingsAsync(configSettings)
//        remoteConfig.setDefaultsAsync(mapOf("darkThemeSupport" to false))
//
//        switch.isVisible = remoteConfig.getBoolean("darkThemeSupport")

//        remoteConfig.fetchAndActivate()
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    val updated = task.result
//                    switch.isVisible = remoteConfig.getBoolean("darkThemeSupport")
//                    Toast.makeText(
//                        this, "Fetch and activate succeeded $updated",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                } else {
//                    Toast.makeText(
//                        this, "Fetch failed",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }

    }

    private fun selectImage(context: Context) {
        val listItems = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle("Take Photo From")
        dialog.setItems(listItems, DialogInterface.OnClickListener { dialogInt, item ->
            when (listItems[item]) {
                "Take Photo" -> {
                    val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, 0)
                }
                "Choose from Gallery" -> {
                    val pickPhotoIntent = Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    startActivityForResult(pickPhotoIntent, 1)
                }
                else -> dialogInt.dismiss()
            }
        })
        dialog.show()
    }

//    private fun isLoggedIn() = FirebaseAuth.getInstance().currentUser == null

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == RC_SIGN_IN) {
//            val response = IdpResponse.fromResultIntent(data)
//
//            if (resultCode == Activity.RESULT_OK) {
//                // Successfully signed in
//                val user = FirebaseAuth.getInstance().currentUser?.phoneNumber
//                Log.d("MainActivity" , "Sign in successful! User : $user")
//                // ...
//            } else {
//                // Sign in failed. If response is null the user canceled the
//                // sign-in flow using the back button. Otherwise check
//                // response.getError().getErrorCode() and handle the error.
//                Log.d("MainActivity" , "Sign in failed!")
//            }
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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

    private fun uploadToStorage(selectedImage: Bitmap) {
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
        return NavigationUI.navigateUp(this.findNavController(R.id.nav_host_fragment), drawer)
    }
}
