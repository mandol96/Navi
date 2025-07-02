package com.cho.navi.ui.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.cho.navi.R

fun ImageView.load(url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(this)
            .load(url)
            .placeholder(R.color.orange_700)
            .error(R.drawable.ic_image_not_supported)
            .into(this)
    } else {
        setImageResource(R.drawable.ic_image_not_supported)
    }
}