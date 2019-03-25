package com.zhuinden.simplestackdemoexamplefragments.util

import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.application.Key

/**
 * Created by Zhuinden on 2019. 03. 20.
 */

class SaferFragmentStateChanger(
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) {
    private val handler: Handler = Handler(Looper.getMainLooper())

    fun handleStateChange(stateChange: StateChange, completionCallback: StateChanger.Callback) {
        tryExecutingFragmentTransaction(stateChange, completionCallback)
    }

    fun stopPendingCallback() {
        val pendingCallback = pendingCallback
        if (pendingCallback != null) {
            handler.removeCallbacks(pendingCallback)
        }
    }

    private var pendingCallback: Runnable? = null

    // we must ensure that we execute the fragment transaction only when no fragments are in transient state
    private fun isAnyFragmentRemoving(stateChange: StateChange): Boolean {
        for (newState in stateChange.getNewState<Key>()) {
            val fragment = fragmentManager.findFragmentByTag(newState.fragmentTag)
            if (fragment != null && fragment.isRemoving) {
                return true
            }
        }
        return false
    }

    private fun tryExecutingFragmentTransaction(stateChange: StateChange, completionCallback: StateChanger.Callback) {
        val anyRemoving = isAnyFragmentRemoving(stateChange)
        if (anyRemoving) {
            pendingCallback = Runnable {
                tryExecutingFragmentTransaction(stateChange, completionCallback)
            }
            handler.postDelayed(pendingCallback, 125L)
        } else {
            executeFragmentTransaction(stateChange, completionCallback)
        }
    }

    private fun executeFragmentTransaction(stateChange: StateChange, completionCallback: StateChanger.Callback) {
        pendingCallback = null

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
        fragmentTransaction.commitNow() // must be `now` in this sample
        completionCallback.stateChangeComplete()
    }
}
