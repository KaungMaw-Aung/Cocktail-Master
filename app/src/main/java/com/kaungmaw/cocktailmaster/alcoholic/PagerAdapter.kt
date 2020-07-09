package com.kaungmaw.cocktailmaster.alcoholic

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kaungmaw.cocktailmaster.alcoholic.tab.TabFragment

const val passedKey = "test"
class PagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val fragment = TabFragment()
        fragment.arguments = Bundle().apply {
            putString(passedKey, when(position){
                0 -> "Alcoholic"
                else -> "Non_Alcoholic"
            })
        }
        return fragment
    }
}