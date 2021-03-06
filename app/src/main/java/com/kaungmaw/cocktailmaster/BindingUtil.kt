package com.kaungmaw.cocktailmaster

import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.kaungmaw.cocktailmaster.domain.DrinkDomain
import com.kaungmaw.cocktailmaster.overview.OverviewAdapter

@BindingAdapter("bindList")
fun bindListWithRecycler(recyclerView: RecyclerView, drinkList: List<DrinkDomain>?) {
    drinkList?.let {
        val adapter = recyclerView.adapter as OverviewAdapter
        adapter.submitList(it)
    }
}

@BindingAdapter("loadImage")
fun loadUrl(imageView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        Glide.with(imageView.context).load(it)
            .apply(
                RequestOptions.placeholderOf(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .into(imageView)
    }
}

@BindingAdapter("bindText")
fun bindText(textView: TextView, text: String?) {
    text?.let {
        textView.text = it
    }
}


@BindingAdapter("bindIngredients")
fun bindIngredients(chipGroup: ChipGroup, ingredients: List<String>?) {
    ingredients?.let {
        chipGroup.removeAllViews()
        val inflater = LayoutInflater.from(chipGroup.context)
        it.map { name ->
            val chip = inflater.inflate(R.layout.ingredient, chipGroup, false) as Chip
            chip.text = name
            chipGroup.addView(chip)
        }
    }
}

fun isLoggedIn(): Boolean = FirebaseAuth.getInstance().currentUser != null
