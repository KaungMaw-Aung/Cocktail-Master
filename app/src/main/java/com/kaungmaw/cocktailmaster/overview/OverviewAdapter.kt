package com.kaungmaw.cocktailmaster.overview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kaungmaw.cocktailmaster.databinding.GridItemHolderBinding
import com.kaungmaw.cocktailmaster.network.Drink

class OverviewAdapter: ListAdapter<Drink,OverviewAdapter.OverviewViewHolder>(DiffCallback()) {

    companion object{
        class DiffCallback: DiffUtil.ItemCallback<Drink>(){
            override fun areItemsTheSame(oldItem: Drink, newItem: Drink): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Drink, newItem: Drink): Boolean {
                return oldItem.drinkID == newItem.drinkID
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OverviewViewHolder {
        return OverviewViewHolder(GridItemHolderBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: OverviewViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    //ViewHolderClass
    class OverviewViewHolder(private val binding: GridItemHolderBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Drink?){
            binding.drinkItem = item
            binding.executePendingBindings()
        }

    }

}