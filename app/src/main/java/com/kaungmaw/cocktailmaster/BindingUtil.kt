package com.kaungmaw.cocktailmaster

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kaungmaw.cocktailmaster.network.Drink
import com.kaungmaw.cocktailmaster.overview.OverviewAdapter

@BindingAdapter("bindList")
fun bindListWithRecycler(recyclerView: RecyclerView , drinkList: List<Drink>?){
    drinkList?.let {
        val adapter = recyclerView.adapter as OverviewAdapter
        adapter.submitList(it)
    }
}

@BindingAdapter("loadImage")
fun loadUrl(imageView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        Glide.with(imageView.context).load(it)
            .apply(RequestOptions.placeholderOf(R.drawable.loading_animation).error(R.drawable.ic_broken_image))
            .into(imageView)
    }
}

@BindingAdapter("bindText")
fun bindText(textView: TextView , text: String?){
    text?.let {
        textView.text = it
    }
}

