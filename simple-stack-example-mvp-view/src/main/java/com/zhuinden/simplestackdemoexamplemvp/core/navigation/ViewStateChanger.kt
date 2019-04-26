package com.zhuinden.simplestackdemoexamplemvp.core.navigation

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestack.navigator.changehandlers.FadeViewChangeHandler

class ViewStateChanger(
    private val activity: Activity,
    private val root: ViewGroup
) : StateChanger {
    override fun handleStateChange(stateChange: StateChange, completionCallback: StateChanger.Callback) {
        val newKey = stateChange.topNewState<ViewKey>()
        val previousKey = stateChange.topPreviousState<ViewKey?>()

        val newView = LayoutInflater.from(stateChange.createContext(activity, newKey))
            .inflate(newKey.layout(), root, false)

        val previousView = root.getChildAt(0)
        Navigator.persistViewToState(previousView)
        Navigator.restoreViewFromState(newView)

        if (previousKey == null || previousView == null) {
            root.addView(newView)
            completionCallback.stateChangeComplete()
            return
        }

        val viewChangeHandler = when (stateChange.direction) {
            StateChange.FORWARD -> newKey.viewChangeHandler()
            StateChange.BACKWARD -> previousKey.viewChangeHandler()
            else -> FadeViewChangeHandler()
        }

        viewChangeHandler.performViewChange(root, previousView, newView, stateChange.direction) {
            completionCallback.stateChangeComplete()
        }
    }
}