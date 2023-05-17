package com.rmaprojects.core.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun buildSnackbar(
    rootView: View,
    text: String,
    duration: Int = Snackbar.LENGTH_SHORT
): Snackbar {
    return Snackbar.make(rootView, text, duration)
}