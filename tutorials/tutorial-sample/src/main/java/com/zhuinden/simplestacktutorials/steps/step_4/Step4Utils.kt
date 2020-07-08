package com.zhuinden.simplestacktutorials.steps.step_4

import android.view.View
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.navigator.Navigator

val View.backstack: Backstack
    get() = Navigator.getBackstack(context)