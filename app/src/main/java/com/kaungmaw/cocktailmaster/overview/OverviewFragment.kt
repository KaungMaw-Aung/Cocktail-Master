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

    private val viewModel by viewModels<OverviewViewModel> { OverviewViewModelFactory(requireNotNull(this.activity).application) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentOverviewBinding.inflate(inflater, container, false)

        val adapter = OverviewAdapter()
        binding.rvCocktailList.adapter = adapter

        viewModel.drinkListResult.observe(viewLifecycleOwner, Observer {
            Log.i("OverviewFragment", "Result size is ${it.size}")
            binding.viewModel = viewModel
        })

        val chipInflater = LayoutInflater.from(requireContext())
        for (each in viewModel.listForChips) {
            chipInflater.inflate(R.layout.category, binding.categoryList, false)
                .let {
                    it as Chip
                }
                .apply {
                    text = each
                    tag = each

                    setOnClickListener {
                        viewModel.savedChip = if (this.isChecked) {
                            adapter.submitList(emptyList())
                            this.tag as String
                        } else ""
                    }

                    setOnCheckedChangeListener { buttonView, isChecked ->
                        viewModel.filterDrink(buttonView.tag as String, isChecked)
                    }
                }.also {
                    binding.categoryList.addView(it)
                }.apply {
                    if (this.tag == viewModel.savedChip) {
                        this.isChecked = true
                    }
                }
        }




        return binding.root
    }

}
