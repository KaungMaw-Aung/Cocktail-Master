package com.kaungmaw.cocktailmaster.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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

        viewModel.responseDetail.observe(viewLifecycleOwner , Observer {
            binding.tvCategoryLabel.visibility = View.VISIBLE
            binding.tvIngredient1.visibility = View.VISIBLE
            binding.detailObject = it.drink[0]
        })

        return binding.root
    }

}