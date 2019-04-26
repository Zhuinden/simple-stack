package com.zhuinden.simplestackdemoexamplemvp.core.navigation

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zhuinden.simplestack.KeyChange
import com.zhuinden.simplestack.KeyChanger
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestack.navigator.changehandlers.FadeViewChangeHandler

class ViewKeyChanger(
    private val activity: Activity,
    private val root: ViewGroup
) : KeyChanger {
    override fun handleKeyChange(keyChange: KeyChange, completionCallback: KeyChanger.Callback) {
        val newKey = keyChange.topNewKey<ViewKey>()
        val previousKey = keyChange.topPreviousKey<ViewKey?>()

        val newView = LayoutInflater.from(keyChange.createContext(activity, newKey))
            .inflate(newKey.layout(), root, false)

        val previousView = root.getChildAt(0)
        Navigator.persistViewToState(previousView)
        Navigator.restoreViewFromState(newView)

        if (previousKey == null || previousView == null) {
            root.addView(newView)
            completionCallback.keyChangeComplete()
            return
        }

        val viewChangeHandler = when (keyChange.direction) {
            KeyChange.FORWARD -> newKey.viewChangeHandler()
            KeyChange.BACKWARD -> previousKey.viewChangeHandler()
            else -> FadeViewChangeHandler()
        }

        viewChangeHandler.performViewChange(root, previousView, newView, keyChange.direction) {
            completionCallback.keyChangeComplete()
        }
    }
}