package com.example.camscan.presentation.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.example.camscan.R

class LoaderView(private val context: Context) {

    private var dialog: Dialog? = null

    init {
        setupDialog()
    }

    private fun setupDialog() {
        dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.loader_view)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
        }
    }

    fun showLoading() {
        dialog?.show()
    }

    fun hideLoading() {
        dialog?.dismiss()
    }
}

