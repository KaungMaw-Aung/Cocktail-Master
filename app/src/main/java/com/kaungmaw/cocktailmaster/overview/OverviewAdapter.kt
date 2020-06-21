package com.kaungmaw.cocktailmaster.overview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kaungmaw.cocktailmaster.databinding.GridItemHolderBinding
import com.kaungmaw.cocktailmaster.domain.DrinkDomain

class OverviewAdapter(private val onClickListener: OnClickListener): ListAdapter<DrinkDomain,OverviewAdapter.OverviewViewHolder>(DiffCallback()) {

    companion object{
        class DiffCallback: DiffUtil.ItemCallback<DrinkDomain>(){
            override fun areItemsTheSame(oldItem: DrinkDomain, newItem: DrinkDomain): Boolean {
                return oldItem.drinkID === newItem.drinkID
            }

            override fun areContentsTheSame(oldItem: DrinkDomain, newItem: DrinkDomain): Boolean {
                return oldItem == newItem
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OverviewViewHolder {
        return OverviewViewHolder(GridItemHolderBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: OverviewViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(item)
        }
    }

    //ViewHolderClass
    class OverviewViewHolder(private val binding: GridItemHolderBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DrinkDomain?){
            binding.drinkItem = item
            binding.executePendingBindings()
        }
    }

    class OnClickListener(val clickListener: (drinkID: String)-> Unit){
        fun onClick(drink: DrinkDomain){
            clickListener(drink.drinkID)
        }
    }

}