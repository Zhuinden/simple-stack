package com.zhuinden.simplestackdemomultistack.core.navigation

import android.view.View
import com.zhuinden.simplestack.Backstack

val View.backstack: Backstack
    get() =
        Backstack.getKey<MultistackViewKey>(context).selectBackstack(context)