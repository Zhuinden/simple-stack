package com.zhuinden.simplestackdemomultistack.core.navigation

import android.os.Bundle
import android.os.Parcelable
import android.view.View

import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.BackstackDelegate
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.StateChanger

import java.util.HashMap
import java.util.LinkedHashMap

/**
 * Created by Zhuinden on 2017.02.19..
 */
class Multistack {
    private val backstackDelegates = LinkedHashMap<String, BackstackDelegate>()
    private var selectedStack: String? = null
    private var stateChanger: StateChanger? = null

    private var isPaused = false

    fun add(identifier: String, backstackDelegate: BackstackDelegate): BackstackDelegate {
        if (selectedStack == null) {
            selectedStack = identifier
        }
        backstackDelegates[identifier] = backstackDelegate
        backstackDelegate.setPersistenceTag(identifier)
        return backstackDelegate
    }

    fun has(identifier: String): Boolean = backstackDelegates.containsKey(identifier)

    fun get(identifier: String): BackstackDelegate {
        return backstackDelegates.get(identifier)!!
    }

    fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            selectedStack = savedInstanceState.getString("selectedStack")
        }
    }

    fun onCreate(identifier: String, savedInstanceState: Bundle?, nonConfigurationInstance: NonConfigurationInstance?, key: Parcelable) {
        get(identifier).onCreate(savedInstanceState,
            nonConfigurationInstance?.getNonConfigInstance(identifier),
            History.single(key))
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putString("selectedStack", selectedStack)
        for (backstackDelegate in backstackDelegates.values) {
            backstackDelegate.onSaveInstanceState(outState)
        }
    }

    fun onRetainCustomNonConfigurationInstance(): Any {
        val nonConfigurationInstance = NonConfigurationInstance()
        for ((key, value) in backstackDelegates) {
            nonConfigurationInstance.putNonConfigInstance(key, value.onRetainCustomNonConfigurationInstance())
        }
        return nonConfigurationInstance
    }

    fun setStateChanger(stateChanger: StateChanger?) {
        this.stateChanger = stateChanger
        for ((key, value) in backstackDelegates) {
            if (key != selectedStack) {
                value.onPause() // FIXME maybe this should be exposed better.
            } else {
                value.setStateChanger(stateChanger)
            }
        }
    }

    fun onPostResume() {
        isPaused = false
        for ((key, value) in backstackDelegates) {
            if (key != selectedStack) {
                value.onPause() // FIXME maybe this should be exposed better.
            } else {
                value.onPostResume()
            }
        }
    }

    fun onPause() {
        isPaused = true
        for ((_, value) in backstackDelegates) {
            value.onPause()
        }
    }

    fun onBackPressed(): Boolean {
        return get(selectedStack!!).onBackPressed()
    }

    fun onDestroy() {
        for ((_, value) in backstackDelegates) {
            value.onDestroy()
        }
    }

    fun setSelectedStack(identifier: String) {
        if (!backstackDelegates.containsKey(identifier)) {
            throw IllegalArgumentException("You cannot specify a stack [$identifier] that does not exist!")
        }
        if (selectedStack != identifier) {
            this.selectedStack = identifier
            setStateChanger(stateChanger)
        }
    }

    class NonConfigurationInstance {
        internal var nonConfigInstances: MutableMap<String, BackstackDelegate.NonConfigurationInstance> = HashMap()

        fun getNonConfigInstance(key: String): BackstackDelegate.NonConfigurationInstance? {
            return nonConfigInstances[key]
        }

        fun putNonConfigInstance(key: String, nonConfigurationInstance: BackstackDelegate.NonConfigurationInstance) {
            nonConfigInstances[key] = nonConfigurationInstance
        }
    }

    fun persistViewToState(view: View?) {
        if (view != null) {
            val key = Backstack.getKey<MultistackKey>(view.context)
            val backstackDelegate = key.selectDelegate(view.context)
            backstackDelegate.persistViewToState(view)
        }
    }

    fun restoreViewFromState(view: View) {
        val key = Backstack.getKey<MultistackKey>(view.context)
        val backstackDelegate = key.selectDelegate(view.context)
        backstackDelegate.restoreViewFromState(view)
    }
}
