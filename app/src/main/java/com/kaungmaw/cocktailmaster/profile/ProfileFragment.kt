package com.kaungmaw.cocktailmaster.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.auth.AuthUI
import com.github.dhaval2404.imagepicker.ImagePicker
import com.kaungmaw.cocktailmaster.R
import com.kaungmaw.cocktailmaster.databinding.FragmentProfileBinding
import com.kaungmaw.cocktailmaster.isLoggedIn
import timber.log.Timber

private const val RC_SIGN_IN = 101

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private val viewModel by viewModels<ProfileViewModel>{
        ProfileViewModelFactory(requireNotNull(this.activity).application)
    }

    // Choose authentication providers
    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.PhoneBuilder().build()
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        FragmentProfileBinding.inflate(inflater, container, false).also { binding = it }
            .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(), RC_SIGN_IN
            )
        }

        binding.btnLogout.setOnClickListener {
            AuthUI.getInstance().signOut(requireContext()).addOnCompleteListener {
                Timber.i("Logout Complete!")
                switchLoginView()
            }
        }

        binding.ivProfile.setOnClickListener { pickImage() }
    }


    override fun onStart() {
        super.onStart()
        switchLoginView()

        viewModel.getUserData()

        viewModel.userProfile.observe(viewLifecycleOwner , Observer {
            binding.tvUsername.text = it.name

            binding.tvEmailLabel.isVisible = (it.email == null).not()
            binding.tvEmail.isVisible = (it.email == null).not()
            it.email?.let { email -> binding.tvEmail.text = email }

            loadProfileUrl(binding.ivProfile, it.photoUrl)
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.saveUserToFireStore()
            } else {
                Timber.d("Sign in failed!")
            }
        }
    }

    private fun switchLoginView() {
        binding.gpLogout.isVisible = isLoggedIn().not()
        binding.gpLogin.isVisible = isLoggedIn()
    }

    private fun loadProfileUrl(imageView: ImageView, imgUrl: String?) {
        imgUrl?.let {
            Glide.with(imageView.context).load(it)
                .circleCrop()
                .apply(
                    RequestOptions.placeholderOf(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image)
                ).into(imageView)
        }
    }

    private fun pickImage(){
        ImagePicker.with(this)
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start { resultCode, data ->
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        ImagePicker.getFile(data)?.let { viewModel.uploadToStorage(it) }
                    }
                    ImagePicker.RESULT_ERROR -> {
                        Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(context, "Task Cancelled", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}
