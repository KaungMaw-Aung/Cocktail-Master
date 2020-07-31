package com.kaungmaw.cocktailmaster.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kaungmaw.cocktailmaster.R
import com.kaungmaw.cocktailmaster.databinding.FragmentProfileBinding
import com.kaungmaw.cocktailmaster.isLoggedIn

private const val RC_SIGN_IN = 101
private const val TAG = "ProfileFragment"

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private val db = Firebase.firestore
    private val docRef = db.collection("users")

    private val viewModel by viewModels<ProfileViewModel>()

    // Choose authentication providers
    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.PhoneBuilder().build()
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.btnLogin.setOnClickListener {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                RC_SIGN_IN
            )
        }

        binding.btnLogout.setOnClickListener {
            AuthUI.getInstance()
                .signOut(requireContext())
                .addOnCompleteListener {
                    Log.i(TAG, "Logout Complete!")
                    switchLoginView()
                }
        }

        binding.ivProfile.setOnClickListener {
            selectImage(requireContext())
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        switchLoginView()

        viewModel.getUserData()

        viewModel.profilePhoto.observe(viewLifecycleOwner, Observer {
            loadProfileUrl(binding.ivProfile, it)
        })

        viewModel.displayName.observe(viewLifecycleOwner, Observer {
            binding.tvUsername.text = it.toString()
        })

        viewModel.email.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                binding.tvEmailLabel.isVisible = false
                binding.tvEmail.isVisible = false
            } else {
                binding.tvEmailLabel.isVisible = true
                binding.tvEmail.isVisible = true
            }
            binding.tvEmail.text = it.toString()
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
//            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                FirebaseAuth.getInstance().currentUser?.uid?.let {
                    docRef.document(it).get().addOnCompleteListener { document ->
                        if (!document.result.exists()) {
                            // Successfully signed in
                            val user = hashMapOf(
                                "display_name" to FirebaseAuth.getInstance().currentUser?.displayName,
                                "email" to FirebaseAuth.getInstance().currentUser?.email,
                                "phone_no" to FirebaseAuth.getInstance().currentUser?.phoneNumber,
                                "profile_url" to (FirebaseAuth.getInstance().currentUser?.photoUrl
                                    ?: "https://bit.ly/39GLBSV")
                            )

                            docRef.document("${FirebaseAuth.getInstance().currentUser?.uid}")
                                .set(user)
                                .addOnSuccessListener {
                                    Log.i(TAG, "Succeed!")
                                }.addOnFailureListener { e ->
                                    Log.i(TAG, "Failed! ${e.message}")
                                }
                        }
                    }
                }
            } else {
                Log.d(TAG, "Sign in failed!")
            }
        }

        if (resultCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                0 -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage = data.extras?.get("data") as Bitmap
                    viewModel.uploadToStorage(selectedImage, docRef)
                }
                1 -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImgUri = data.data
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(
                            requireContext().contentResolver,
                            selectedImgUri!!
                        )
                    viewModel.uploadToStorage(bitmap, docRef)
                }
            }
        }
    }

    private fun switchLoginView() {
        if (isLoggedIn()) {
            binding.tvLoginLabel.isVisible = false
            binding.btnLogin.isVisible = false
            binding.aniFavorite.isVisible = true
            binding.ivProfile.isVisible = true
            binding.tvUsername.isVisible = true
            binding.btnLogout.isVisible = true
            binding.tvEmailLabel.isVisible = true
            binding.tvEmail.isVisible = true
        } else {
            binding.tvLoginLabel.isVisible = true
            binding.btnLogin.isVisible = true
            binding.aniFavorite.isVisible = false
            binding.ivProfile.isVisible = false
            binding.tvUsername.isVisible = false
            binding.btnLogout.isVisible = false
            binding.tvEmailLabel.isVisible = false
            binding.tvEmail.isVisible = false
        }
    }

    private fun loadProfileUrl(imageView: ImageView, imgUrl: String?) {
        imgUrl?.let {
            Glide.with(imageView.context).load(it)
                .circleCrop()
                .apply(
                    RequestOptions.placeholderOf(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image)
                )
                .into(imageView)
        }
    }

    private fun selectImage(context: Context) {
        val listItems = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle("Choose Action")
        dialog.setItems(listItems) { dialogInt, item ->
            when (listItems[item]) {
                "Take Photo" -> {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
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

}
