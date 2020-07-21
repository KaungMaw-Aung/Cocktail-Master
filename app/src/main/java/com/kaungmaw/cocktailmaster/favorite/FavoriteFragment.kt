package com.kaungmaw.cocktailmaster.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.kaungmaw.cocktailmaster.R
import com.kaungmaw.cocktailmaster.databinding.FragmentFavoriteBinding
import com.kaungmaw.cocktailmaster.overview.OverviewAdapter
import com.kaungmaw.cocktailmaster.overview.OverviewFragmentDirections

class FavoriteFragment : Fragment() {

    private val viewModel by viewModels<FavoriteViewModel> {
        FavoriteViewModelFactory(requireNotNull(this.activity).application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        val spanCount = resources.getInteger(R.integer.span_count)
        val layoutManager = GridLayoutManager(requireContext(),spanCount)
        binding.rvFavorite.layoutManager = layoutManager

        val adapter = OverviewAdapter(OverviewAdapter.OnClickListener{
//            findNavController().navigate(OverviewFragmentDirections.)
        })

        binding.rvFavorite.adapter = adapter

        viewModel.favoriteList.observe(viewLifecycleOwner , Observer {
            binding.favoriteViewModel = viewModel
        })




        return binding.root
    }
}