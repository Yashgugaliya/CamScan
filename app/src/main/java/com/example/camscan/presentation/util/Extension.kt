package com.example.camscan.presentation.util

import android.text.Layout
import android.view.View
import coil.load
import com.example.camscan.presentation.ui.custom.BoundingBoxImageView

fun BoundingBoxImageView.load(url: String) {
  this.load(url) { crossfade(true) }
}

fun View.visible() {
  this.visibility = View.VISIBLE
}

fun View.gone() {
  this.visibility = View.GONE
}

