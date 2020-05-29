package com.zhuinden.simplestackdemomultistack.core.navigation

import com.zhuinden.simplestack.Backstack

val BaseFragment.backstack: Backstack
    get() =
        getKey<MultistackFragmentKey>().selectBackstack(requireContext())