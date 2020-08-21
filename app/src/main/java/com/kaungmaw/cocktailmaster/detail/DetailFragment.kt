package com.kaungmaw.cocktailmaster.detail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.kaungmaw.cocktailmaster.databinding.FragmentDetaillBinding
import com.kaungmaw.cocktailmaster.isLoggedIn

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetaillBinding

    private val viewModel by viewModels<DetailViewModel> { DetailViewModelFactory(
            requireNotNull(this.activity).application, DetailFragmentArgs.fromBundle(requireArguments()).drinkID
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = FragmentDetaillBinding.inflate(inflater,container,false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.responseDetail.observe(viewLifecycleOwner, Observer {
            binding.apply {
                detailObject = it
                tvCategoryLabel.visibility = View.VISIBLE
                tvIngredientsLabel.visibility = View.VISIBLE
                tvInstructionLabel.visibility = View.VISIBLE
                aniFavorite.visibility = View.VISIBLE
            }
        })

        viewModel.firstFavCheck.observe(viewLifecycleOwner, Observer {
            when (it) {
                true -> binding.aniFavorite.setMinAndMaxFrame(179, 179)
                else -> binding.aniFavorite.setMinAndMaxFrame(0, 0)
            }
        })


        binding.aniFavorite.setOnClickListener {
            if (isLoggedIn()) {
                viewModel.switchFavorite()
            } else {
                Toast.makeText(
                    requireContext(), "Please log in to give favorite!", Toast.LENGTH_LONG
                ).show()
            }
        }

        viewModel.isFavorite.observe(viewLifecycleOwner, Observer {
            when (it) {
                true -> {
                    binding.aniFavorite.setMinAndMaxFrame(70, 179)
                    binding.aniFavorite.speed = 3F
                    binding.aniFavorite.playAnimation()
                    Snackbar.make(binding.tvDetailName, "add favorite", Snackbar.LENGTH_SHORT).show()
                }
                else -> {
                    binding.aniFavorite.setMinAndMaxFrame(0, 0)
                    binding.aniFavorite.playAnimation()
                    Snackbar.make(binding.tvDetailName, "remove favorite", Snackbar.LENGTH_SHORT).show()
                }
            }
        })
    }
}
