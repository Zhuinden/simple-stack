package com.zhuinden.simplestackdemoexamplemvp.core.navigation

import android.view.View
import com.zhuinden.simplestack.Backstack

fun <K : ViewKey> View.getKey() = Backstack.getKey<K>(context)