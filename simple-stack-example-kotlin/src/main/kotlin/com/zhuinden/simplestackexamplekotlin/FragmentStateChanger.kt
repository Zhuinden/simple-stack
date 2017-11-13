package com.zhuinden.simplestackexamplekotlin

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.zhuinden.simplestack.StateChange

/**
 * Created by Owner on 2017.11.13.
 */

class FragmentStateChanger(private val fragmentManager: FragmentManager, private val containerId: Int) {

    @SuppressLint("CommitTransaction") // lint does not detect it in the apply block.
    fun handleStateChange(stateChange: StateChange) {
        val fragmentTransaction = fragmentManager.beginTransaction().disallowAddToBackStack()
        fragmentTransaction.apply {
            when(stateChange.direction) {
                StateChange.FORWARD -> {
                    setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                }
                StateChange.BACKWARD -> {
                    setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right, R.anim.slide_in_from_left, R.anim.slide_out_to_right)
                }
            }
            val previousState = stateChange.getPreviousState<BaseKey>()
            val newState = stateChange.getNewState<BaseKey>()
            for (oldKey in previousState) {
                val fragment = fragmentManager.findFragmentByTag(oldKey.fragmentTag)
                if (fragment != null) {
                    if (!newState.contains(oldKey)) {
                        remove(fragment)
                    } else if (!fragment.isDetached) {
                        detach(fragment)
                    }
                }
            }
            for (newKey in newState) {
                var fragment: Fragment? = fragmentManager.findFragmentByTag(newKey.fragmentTag)
                if (newKey == stateChange.topNewState<Any>()) {
                    if (fragment != null) {
                        if (fragment.isDetached) {
                            attach(fragment)
                        }
                    } else {
                        fragment = newKey.newFragment()
                        add(containerId, fragment, newKey.fragmentTag)
                    }
                } else {
                    if (fragment != null && !fragment.isDetached) {
                        detach(fragment)
                    }
                }
            }
            commitNow()
        }
    }
}