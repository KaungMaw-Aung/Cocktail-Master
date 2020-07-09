package com.kaungmaw.cocktailmaster.alcoholic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kaungmaw.cocktailmaster.R

class AlcoholicFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_alcoholic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = PagerAdapter(this)
        val viewPager = view.findViewById(R.id.viewPager) as ViewPager2
        viewPager.adapter = adapter

        val tabBar = view.findViewById(R.id.tabBar) as TabLayout
        TabLayoutMediator(tabBar,viewPager){  tab, position ->
            if (position == 0){
                tab.text = "Alcoholic"
            }else{
                tab.text = "Non Alcoholic"
            }
        }.attach()
    }
}