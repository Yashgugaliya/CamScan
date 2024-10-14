package com.example.camscan.presentation.util

import android.view.View
import coil.Coil.imageLoader
import coil.request.ImageRequest
import com.example.camscan.presentation.ui.custom.BoundingBoxImageView

fun BoundingBoxImageView.loadImageFromPath(path: String, onLoadComplete: (() -> Unit)? = null) {
  val request = ImageRequest.Builder(context)
    .data(path)
    .target { drawable ->
      setImageDrawable(drawable)
      invalidate()
      onLoadComplete?.invoke()
    }
    .build()
  imageLoader(context).enqueue(request)
}

fun View.visible() {
  this.visibility = View.VISIBLE
}

fun View.gone() {
  this.visibility = View.GONE
}

