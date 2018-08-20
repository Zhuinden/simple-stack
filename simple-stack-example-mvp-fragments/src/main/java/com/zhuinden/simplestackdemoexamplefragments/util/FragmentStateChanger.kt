package com.zhuinden.simplestackdemoexamplefragments.util

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.application.Key

/**
 * Created by Owner on 2017. 02. 03..
 */

class FragmentStateChanger(
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) {
    fun handleStateChange(stateChange: StateChange) {
        val fragmentTransaction = fragmentManager.beginTransaction().disallowAddToBackStack()
        when(stateChange.direction) {
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

        val previousState = stateChange.getPreviousState<Key>()
        val newState = stateChange.getNewState<Key>()
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
            if (newKey == stateChange.topNewState<Any>()) {
                if (fragment != null) {
                    if (fragment.isHidden) {
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
        fragmentTransaction.commitNow()
    }
}
