package com.skybound.space.core.imageloader

import android.widget.ImageView
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation

class CoilImageLoader : ImageLoader {

    override fun load(imageView: ImageView, url: String?) {
        imageView.load(url)
    }

    override fun loadCircle(imageView: ImageView, url: String?) {
        imageView.load(url) {
            transformations(CircleCropTransformation())
        }
    }

    override fun loadRounded(imageView: ImageView, url: String?, radiusDp: Int) {
        val radiusPx = radiusDp * imageView.resources.displayMetrics.density
        imageView.load(url) {
            transformations(RoundedCornersTransformation(radiusPx))
        }
    }

    override fun loadWithPlaceholder(imageView: ImageView, url: String?, placeholderRes: Int) {
        imageView.load(url) {
            placeholder(placeholderRes)
            error(placeholderRes)
        }
    }
}
