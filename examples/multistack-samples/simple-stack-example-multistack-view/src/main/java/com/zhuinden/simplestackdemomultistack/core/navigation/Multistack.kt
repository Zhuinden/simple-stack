package com.zhuinden.simplestackdemomultistack.core.navigation

import android.view.View
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.StateChanger
import com.zhuinden.statebundle.StateBundle
import java.util.*

class Multistack : Bundleable {
    private val backstacks = LinkedHashMap<String, Backstack>()
    private var selectedStack: String? = null
    private var stateChanger: StateChanger? = null

    private var isPaused = false

    fun add(initialKey: MultistackViewKey) {
        val identifier = initialKey.stackIdentifier()

        if (has(identifier)) {
            throw IllegalArgumentException("The identifier [$identifier] is already registered to the multistack")
        }
        if (selectedStack == null) {
            selectedStack = identifier
        }
        val backstack = Backstack().apply {
            setup(History.of(initialKey))
        }
        backstacks[identifier] = backstack
    }

    fun has(identifier: String): Boolean = backstacks.containsKey(identifier)

    fun get(identifier: String): Backstack {
        return backstacks.get(identifier)!!
    }

    fun getSelectedStack(): Backstack = backstacks.get(selectedStack)!!

    fun setStateChanger(stateChanger: StateChanger?) {
        this.stateChanger = stateChanger
        for ((key, value) in backstacks) {
            if (key != selectedStack) {
                value.detachStateChanger()
            } else {
                value.setStateChanger(stateChanger)
            }
        }
    }

    fun unpause() {
        isPaused = false
        for ((key, value) in backstacks) {
            if (key != selectedStack) {
                value.detachStateChanger()
            } else {
                value.reattachStateChanger()
            }
        }
    }

    fun pause() {
        isPaused = true
        for ((_, value) in backstacks) {
            value.detachStateChanger()
        }
    }

    fun executePendingStateChange() {
        for ((_, backstack) in backstacks) {
            backstack.executePendingStateChange()
        }
    }

    fun finalize() {
        for ((_, backstack) in backstacks) {
            backstack.finalizeScopes()
        }
    }

    fun setSelectedStack(identifier: String) {
        if (!backstacks.containsKey(identifier)) {
            throw IllegalArgumentException("You cannot specify a stack [$identifier] that does not exist!")
        }
        if (selectedStack != identifier) {
            this.selectedStack = identifier
            setStateChanger(stateChanger)
        }
    }

    fun persistViewToState(view: View?) {
        if (view != null) {
            val key = Backstack.getKey<MultistackViewKey>(view.context)
            val backstackDelegate = key.selectBackstack(view.context)
            backstackDelegate.persistViewToState(view)
        }
    }

    fun restoreViewFromState(view: View) {
        val key = Backstack.getKey<MultistackViewKey>(view.context)
        val backstack = key.selectBackstack(view.context)
        backstack.restoreViewFromState(view)
    }

    override fun toBundle(): StateBundle = StateBundle().apply {
        putString("multistack_selectedStack", selectedStack)
        for ((identifier, backstack) in backstacks.entries) {
            putBundle("multistack_identifier_$identifier", backstack.toBundle())
        }
    }

    override fun fromBundle(bundle: StateBundle?) {
        bundle?.run {
            selectedStack = getString("multistack_selectedStack")
            for ((identifier, backstack) in backstacks.entries) {
                backstack.fromBundle(getBundle("multistack_identifier_$identifier"))
            }
        }
    }
}
