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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kaungmaw.cocktailmaster.R
import com.kaungmaw.cocktailmaster.databinding.FragmentDetaillBinding

class DetailFragment : Fragment() {

    private val viewModel by viewModels<DetailViewModel> {
        DetailViewModelFactory(
            requireNotNull(
                this.activity
            ).application, DetailFragmentArgs.fromBundle(requireArguments()).drinkID
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentDetaillBinding.inflate(inflater, container, false)

        viewModel.responseDetail.observe(viewLifecycleOwner, Observer {
            binding.apply {
                detailObject = it
                tvCategoryLabel.visibility = View.VISIBLE
                tvIngredientsLabel.visibility = View.VISIBLE
                tvInstructionLabel.visibility = View.VISIBLE
                ivFavorite!!.visibility = View.VISIBLE
            }
        })

        viewModel.isFavorite.observe(viewLifecycleOwner , Observer {
            binding.ivFavorite?.setImageResource(if (it == true) {
                R.drawable.ic_favorite
            }else{
                R.drawable.ic_non_favorite
            })
        })

        binding.ivFavorite?.setOnClickListener {
            viewModel.toggleFavorite()
        }

        return binding.root
    }

}
