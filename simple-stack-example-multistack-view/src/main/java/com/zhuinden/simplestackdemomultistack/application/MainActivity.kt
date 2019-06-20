package com.zhuinden.simplestackdemomultistack.application

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View

import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.BackstackDelegate
import com.zhuinden.simplestack.KeyContextWrapper
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import com.zhuinden.simplestackdemomultistack.R
import com.zhuinden.simplestackdemomultistack.features.main.chromecast.ChromeCastKey
import com.zhuinden.simplestackdemomultistack.features.main.cloudsync.CloudSyncKey
import com.zhuinden.simplestackdemomultistack.features.main.list.ListKey
import com.zhuinden.simplestackdemomultistack.features.main.mail.MailKey

import it.sephiroth.android.library.bottomnavigation.BottomNavigation

import com.zhuinden.simplestackdemomultistack.application.MainActivity.StackType.CHROMECAST
import com.zhuinden.simplestackdemomultistack.application.MainActivity.StackType.CLOUDSYNC
import com.zhuinden.simplestackdemomultistack.application.MainActivity.StackType.LIST
import com.zhuinden.simplestackdemomultistack.application.MainActivity.StackType.MAIL
import com.zhuinden.simplestackdemomultistack.core.navigation.MultistackKey
import com.zhuinden.simplestackdemomultistack.core.navigation.Multistack
import com.zhuinden.simplestackdemomultistack.util.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), StateChanger {
    lateinit var multistack: Multistack

    private var isAnimating: Boolean = false

    enum class StackType {
        CLOUDSYNC,
        CHROMECAST,
        MAIL,
        LIST
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        this.multistack = Multistack()

        multistack.add(CLOUDSYNC.name, BackstackDelegate())
        multistack.add(CHROMECAST.name, BackstackDelegate())
        multistack.add(MAIL.name, BackstackDelegate())
        multistack.add(LIST.name, BackstackDelegate())

        val nonConfigurationInstance = lastCustomNonConfigurationInstance as Multistack.NonConfigurationInstance?

        multistack.onCreate(savedInstanceState)

        multistack.onCreate(CLOUDSYNC.name, savedInstanceState, nonConfigurationInstance, CloudSyncKey.create())
        multistack.onCreate(CHROMECAST.name, savedInstanceState, nonConfigurationInstance, ChromeCastKey.create())
        multistack.onCreate(MAIL.name, savedInstanceState, nonConfigurationInstance, MailKey.create())
        multistack.onCreate(LIST.name, savedInstanceState, nonConfigurationInstance, ListKey.create())

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        bottomNavigationView.setOnMenuItemClickListener(object : BottomNavigation.OnMenuItemSelectionListener {
            override fun onMenuItemSelect(@IdRes menuItemId: Int, itemIndex: Int, b: Boolean) {
                multistack.setSelectedStack(StackType.values()[itemIndex].name)
            }

            override fun onMenuItemReselect(@IdRes menuItemId: Int, itemIndex: Int, b: Boolean) {

            }
        })
        multistack.setStateChanger(this)
    }

    override fun onRetainCustomNonConfigurationInstance(): Any {
        return multistack.onRetainCustomNonConfigurationInstance()
    }

    override fun onPostResume() {
        super.onPostResume()
        multistack.onPostResume()
    }

    override fun onBackPressed() {
        if (!multistack.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onPause() {
        multistack.onPause()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        multistack.persistViewToState(root.getChildAt(0))
        multistack.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        multistack.onDestroy()
        super.onDestroy()
    }

    override fun getSystemService(name: String): Any? {
        if (::multistack.isInitialized) {
            if (multistack.has(name)) {
                return multistack.get(name)
            }
        }
        return super.getSystemService(name)
    }

    private fun exchangeViewForKey(newKey: MultistackKey, direction: Int) {
        multistack.persistViewToState(root.getChildAt(0))
        multistack.setSelectedStack(newKey.stackIdentifier())
        val newContext = KeyContextWrapper(this, newKey)
        val previousView: View? = root.getChildAt(0)
        val newView = LayoutInflater.from(newContext).inflate(newKey.layout(), root, false)
        multistack.restoreViewFromState(newView)
        root.addView(newView)

        if (previousView == null || direction == StateChange.REPLACE) {
            finishStateChange(previousView)
        } else {
            isAnimating = true
            newView.waitForMeasure { _, _, _ ->
                runAnimation(previousView, newView, direction, object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        isAnimating = false
                        finishStateChange(previousView)
                    }
                })
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return !isAnimating && super.dispatchTouchEvent(ev)
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
            val previousStack = StackType.valueOf(previousKey.stackIdentifier())
            val newStack = StackType.valueOf((stateChange.topNewKey<Any>() as MultistackKey).stackIdentifier())
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