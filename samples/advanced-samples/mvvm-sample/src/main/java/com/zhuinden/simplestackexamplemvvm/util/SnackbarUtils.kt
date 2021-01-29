package com.zhuinden.simplestackexamplemvvm.util


import android.view.View
import com.google.android.material.snackbar.Snackbar

fun showSnackbar(v: View, snackbarText: String) {
    Snackbar.make(v, snackbarText, Snackbar.LENGTH_LONG).show()
}