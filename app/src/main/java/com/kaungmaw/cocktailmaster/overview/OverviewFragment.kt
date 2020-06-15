package com.kaungmaw.cocktailmaster.overview

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import com.kaungmaw.cocktailmaster.R

import com.kaungmaw.cocktailmaster.databinding.FragmentOverviewBinding

/**
 * A simple [Fragment] subclass.
 */
class OverviewFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentOverviewBinding.inflate(inflater, container, false)
        val viewModel by viewModels<OverviewViewModel>()

        viewModel.drinkListResult.observe(viewLifecycleOwner, Observer {
            Log.i("OverviewFragment", "Result size is ${it.drinksList.size}")
            binding.responseObjDto = it
        })

        viewModel.listForChips.observe(viewLifecycleOwner, Observer<List<String>> { list ->
            val chipGroup = binding.categoryList
            val chipInflater = LayoutInflater.from(chipGroup.context)
            val children = list.map {
                val chip = chipInflater.inflate(R.layout.category, chipGroup, false) as Chip
                chip.text = it
                chip.tag = it
                chip.setOnCheckedChangeListener { buttonView, isChecked ->
                    viewModel.filterDrink(buttonView.tag as String, isChecked)
                }
                chip
            }
            chipGroup.removeAllViews()
            for (each in children) {
                chipGroup.addView(each)
            }
        })

        val adapter = OverviewAdapter()

        binding.rvCocktailList.adapter = adapter

        return binding.root
    }

}
