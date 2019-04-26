package com.zhuinden.simplestackkotlindaggerexample

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.zhuinden.simplestack.KeyChange

class FragmentKeyChanger(
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) {
    fun handleKeyChange(keyChange: KeyChange) {
        val fragmentTransaction = fragmentManager.beginTransaction().apply {
            when (keyChange.direction) {
                KeyChange.FORWARD -> {
                    setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                }
                KeyChange.BACKWARD -> {
                    setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right, R.anim.slide_in_from_left, R.anim.slide_out_to_right)
                }
            }
            val previousState = keyChange.getPreviousKeys<BaseKey>()
            val newState = keyChange.getNewKeys<BaseKey>()
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
                if (newKey == keyChange.topNewKey<Any>()) {
                    if (fragment != null) {
                        if (fragment.isRemoving) { // Fragments are quirky, they die asynchronously. Ignore if they're still there.
                            replace(containerId, newKey.newFragment(), newKey.fragmentTag)
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
    }
}