package com.zhuinden.simplestackdemomultistack.core.navigation

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import com.zhuinden.simplestackdemomultistack.R

class MultistackFragmentStateChanger(
    private val containerId: Int,
    private val fragmentManager: FragmentManager
) : StateChanger {
    override fun handleStateChange(stateChange: StateChange, completionCallback: StateChanger.Callback) {
        if (stateChange.isTopNewKeyEqualToPrevious) {
            // no-op
            completionCallback.stateChangeComplete()
            return
        }

        val previousState = stateChange.getPreviousKeys<MultistackFragmentKey>()
        val newState = stateChange.getNewKeys<MultistackFragmentKey>()

        val fragmentTransaction = fragmentManager.beginTransaction()

        // detach fragment not in this backstack
        var topFragment: Fragment? = null

        val allFragments = fragmentManager.fragments // can't trust findFragmentById.
        @Suppress("SENSELESS_COMPARISON")
        if (allFragments != null) {
            for (i in allFragments.size - 1 downTo 0) {
                val candidate = allFragments[i]
                if (candidate != null && candidate.id == containerId && candidate.isAdded && candidate.view != null && !candidate.isDetached && !candidate.isHidden) {
                    topFragment = candidate
                    break
                }
            }
        }

        if (topFragment != null) {
            var found = false
            for (previousKey in previousState) {
                val fragment: Fragment? = fragmentManager.findFragmentByTag(previousKey.fragmentTag)
                if (topFragment === fragment) {
                    found = true
                    break
                }
            }

            if (!found) {
                for (newKey in newState) {
                    val fragment: Fragment? = fragmentManager.findFragmentByTag(newKey.fragmentTag)
                    if (topFragment === fragment) {
                        found = true
                        break
                    }
                }
            }

            if (!found) { // this fragment belongs to a different backstack, so we can safely hide it.
                fragmentTransaction.detach(topFragment)
            }
        }
        // end detach fragment not in this backstack

        val previousKey = stateChange.topPreviousKey<MultistackFragmentKey>()

        fragmentTransaction.apply {
            when (stateChange.direction) {
                StateChange.FORWARD -> {
                    setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                }
                StateChange.BACKWARD -> {
                    setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right, R.anim.slide_in_from_left, R.anim.slide_out_to_right)
                }
                StateChange.REPLACE -> {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                }
            }

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
                if (newKey == stateChange.topNewKey<Any>()) {
                    if (fragment != null) {
                        if (fragment.isRemoving) { // Fragments are quirky, they die asynchronously. Ignore if they're still there.
                            fragment = newKey.newFragment()
                            replace(containerId, fragment, newKey.fragmentTag)
                        } else if (fragment.isDetached) {
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
        }
        fragmentTransaction.commitAllowingStateLoss()

        completionCallback.stateChangeComplete()
    }
}
