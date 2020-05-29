package com.zhuinden.simplestackdemoexamplefragments.core.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestackdemoexamplefragments.R

/**
 * Created by Zhuinden on 2019. 03. 20.
 */

class FragmentStateChanger(
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) {
    fun handleStateChange(stateChange: StateChange) {
        fragmentManager.executePendingTransactions()

        val fragmentTransaction = fragmentManager.beginTransaction().disallowAddToBackStack()
        when (stateChange.direction) {
            StateChange.FORWARD -> {
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
            }
            StateChange.BACKWARD -> {
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right)
            }
            else -> {
                /* do nothing */
            }
        }

        val previousState = stateChange.getPreviousKeys<FragmentKey>()
        val newState = stateChange.getNewKeys<FragmentKey>()
        for (oldKey in previousState) {
            val fragment = fragmentManager.findFragmentByTag(oldKey.fragmentTag)
            if (fragment != null) {
                if (!newState.contains(oldKey)) {
                    fragmentTransaction.remove(fragment)
                } else if (!fragment.isHidden) {
                    fragmentTransaction.hide(fragment)
                }
            }
        }
        for (newKey in newState) {
            val fragment: Fragment? = fragmentManager.findFragmentByTag(newKey.fragmentTag)
            if (newKey == stateChange.topNewKey<Any>()) {
                if (fragment != null) {
                    if (fragment.isRemoving) { // Fragments are quirky, they die asynchronously. Ignore if they're still there.
                        fragmentTransaction.replace(containerId, newKey.newFragment(), newKey.fragmentTag)
                    } else if (fragment.isHidden) {
                        fragmentTransaction.show(fragment)
                    }
                } else {
                    fragmentTransaction.add(containerId, newKey.newFragment(), newKey.fragmentTag)
                }
            } else {
                if (fragment != null && !fragment.isHidden) {
                    fragmentTransaction.hide(fragment)
                }
            }
        }
        fragmentTransaction.commitNow() // must be `now` in this sample
    }
}
