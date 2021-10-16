package com.zhuinden.simplestackextensionscomposesample.core

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentStateChanger

class FragmentStateChanger(
    fragmentManager: FragmentManager,
    @IdRes containerId: Int,
) : DefaultFragmentStateChanger(fragmentManager, containerId) {
    override fun onReplaceNavigation(fragmentTransaction: FragmentTransaction, stateChange: StateChange) {
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
    }
}