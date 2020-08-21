package com.kaungmaw.cocktailmaster.overview

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
import com.kaungmaw.cocktailmaster.bindListWithRecycler
import com.kaungmaw.cocktailmaster.databinding.FragmentOverviewBinding

class OverviewFragment : Fragment() {

    private val viewModel by viewModels<OverviewViewModel> {
        OverviewViewModelFactory(requireNotNull(this.activity).application)
    }

    private lateinit var binding: FragmentOverviewBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        FragmentOverviewBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spanCount = resources.getInteger(R.integer.span_count)
        val layoutManager = GridLayoutManager(requireContext(), spanCount)
        binding.rvCocktailList.layoutManager = layoutManager

        val adapter = OverviewAdapter(OverviewAdapter.OnClickListener {
            findNavController().navigate(OverviewFragmentDirections.actionOverviewFragmentToDetailFragment(it))
        })

        binding.rvCocktailList.adapter = adapter

        binding.categoryList.setOnCheckedChangeListener { _, checkedId ->
            getFilterFromChipId(checkedId).also {
                viewModel.filter(it)
                viewModel.currentFilter.value = it
            }
        }

        viewModel.drinkListResult.observe(viewLifecycleOwner, Observer {
            bindListWithRecycler(binding.rvCocktailList,it)
        })

    }

    private fun getFilterFromChipId(checkId: Int): String = when (checkId) {
        R.id.chip_cocktail -> "Cocktail"
        R.id.chip_ordinary -> "Ordinary Drink"
        R.id.chip_milk_float_shake -> "Milk / Float / Shake"
        R.id.chip_cocoa -> "Cocoa"
        R.id.chip_shot -> "Shot"
        R.id.chip_coffee_tea -> "Coffee / Tea"
        R.id.chip_homemade -> "Homemade Liqueur"
        R.id.chip_punch_party_drink -> "Punch / Party Drink"
        R.id.chip_beer -> "Beer"
        R.id.chip_soft_drink -> "Soft Drink / Soda"
        else -> "Other/Unknown"
    }

}
