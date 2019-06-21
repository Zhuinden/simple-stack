package com.zhuinden.simplestackdemomultistack.core.navigation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.KeyContextWrapper
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import com.zhuinden.simplestackdemomultistack.application.MainActivity
import com.zhuinden.simplestackdemomultistack.util.animateTogether
import com.zhuinden.simplestackdemomultistack.util.f
import com.zhuinden.simplestackdemomultistack.util.objectAnimate
import com.zhuinden.simplestackdemomultistack.util.waitForMeasure

class MultistackViewStateChanger(
    private val activity: Activity,
    private val multistack: Multistack,
    private val root: ViewGroup,
    private val animationStateListener: AnimationStateListener
) : StateChanger {
    interface AnimationStateListener {
        fun onAnimationStarted()

        fun onAnimationEnded()
    }

    private fun exchangeViewForKey(newKey: MultistackKey, direction: Int) {
        multistack.persistViewToState(root.getChildAt(0))
        multistack.setSelectedStack(newKey.stackIdentifier())
        val newContext = KeyContextWrapper(activity, newKey)
        val previousView: View? = root.getChildAt(0)
        val newView = LayoutInflater.from(newContext).inflate(newKey.layout(), root, false)
        multistack.restoreViewFromState(newView)
        root.addView(newView)

        if (previousView == null || direction == StateChange.REPLACE) {
            finishStateChange(previousView)
        } else {
            animationStateListener.onAnimationStarted()
            newView.waitForMeasure { _, _, _ ->
                runAnimation(previousView, newView, direction, object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        animationStateListener.onAnimationEnded()
                        finishStateChange(previousView)
                    }
                })
            }
        }
    }


    override fun handleStateChange(stateChange: StateChange, completionCallback: StateChanger.Callback) {
        if (stateChange.isTopNewKeyEqualToPrevious) {
            // no-op
            completionCallback.stateChangeComplete()
            return
        }
        var direction = StateChange.REPLACE

        val currentView: View? = root.getChildAt(0)

        if (currentView != null) {
            val previousKey = Backstack.getKey<MultistackKey>(currentView.context)
            val previousStack = MainActivity.StackType.valueOf(previousKey.stackIdentifier())
            val newStack = MainActivity.StackType.valueOf((stateChange.topNewKey<Any>() as MultistackKey).stackIdentifier())
            direction = when {
                previousStack.ordinal < newStack.ordinal -> StateChange.FORWARD
                previousStack.ordinal > newStack.ordinal -> StateChange.BACKWARD
                else -> StateChange.REPLACE
            }
        }
        exchangeViewForKey(stateChange.topNewKey(), direction)
        completionCallback.stateChangeComplete()
    }

    private fun finishStateChange(previousView: View?) {
        if (previousView != null) {
            root.removeView(previousView)
        }
    }

    // animation
    private fun runAnimation(previousView: View, newView: View, direction: Int, animatorListenerAdapter: AnimatorListenerAdapter) {
        val animator = createSegue(previousView, newView, direction)
        animator.addListener(animatorListenerAdapter)
        animator.start()
    }

    private fun createSegue(from: View, to: View, direction: Int): Animator = run {
        val fromTranslation = -1 * direction * from.width
        val toTranslation = direction * to.width

        to.translationX = toTranslation.f
        animateTogether(
            from.objectAnimate().translationX(fromTranslation.f).get(),
            to.objectAnimate().translationX(0.f).get()
        )
    }
}
