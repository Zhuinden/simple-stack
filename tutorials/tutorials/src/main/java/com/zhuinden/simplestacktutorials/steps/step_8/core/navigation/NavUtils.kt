package com.zhuinden.simplestacktutorials.steps.step_8.core.navigation

import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.navigator.Navigator

val Fragment.backstack: Backstack
    get() = Navigator.getBackstack(requireContext())