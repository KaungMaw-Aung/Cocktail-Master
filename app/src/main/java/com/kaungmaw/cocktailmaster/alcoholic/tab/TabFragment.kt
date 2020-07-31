package com.kaungmaw.cocktailmaster.alcoholic.tab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.kaungmaw.cocktailmaster.R
import com.kaungmaw.cocktailmaster.alcoholic.AlcoholicFragmentDirections
import com.kaungmaw.cocktailmaster.alcoholic.passedKey
import com.kaungmaw.cocktailmaster.databinding.FragmentTabBinding
import com.kaungmaw.cocktailmaster.overview.OverviewAdapter
import com.kaungmaw.cocktailmaster.repository.CocktailRepository

class TabFragment : Fragment() {

    private lateinit var binding: FragmentTabBinding
    private val viewModel by viewModels<TabViewModel> { TabViewModelFactory(requireNotNull(this.activity).application) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentTabBinding.inflate(LayoutInflater.from(requireContext()), container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireArguments().takeIf { it.containsKey(passedKey) }?.let {
            it.getString(passedKey)?.let { type ->
                viewModel.filterAlcoholic(type)
            }
        }

        viewModel.alcoholicResponse.observe(viewLifecycleOwner , Observer {
            val adapter = OverviewAdapter(OverviewAdapter.OnClickListener { id ->
                findNavController().navigate(AlcoholicFragmentDirections.actionAlcoholicFragmentToDetailFragment(id))
            })
            adapter.submitList(it)
            binding.rvAlcoholicList.adapter = adapter

        })
    }
}