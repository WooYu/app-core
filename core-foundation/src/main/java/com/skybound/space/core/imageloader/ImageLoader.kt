package com.skybound.space.core.imageloader

import android.widget.ImageView

interface ImageLoader {
    fun load(imageView: ImageView, url: String?)
    fun loadCircle(imageView: ImageView, url: String?)
    fun loadRounded(imageView: ImageView, url: String?, radiusDp: Int)
    fun loadWithPlaceholder(imageView: ImageView, url: String?, placeholderRes: Int)
}
