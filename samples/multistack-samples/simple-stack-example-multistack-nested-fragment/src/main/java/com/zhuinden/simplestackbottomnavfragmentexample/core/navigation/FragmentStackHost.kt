package com.zhuinden.simplestackbottomnavfragmentexample.core.navigation

import com.zhuinden.simplestack.*
import com.zhuinden.statebundle.StateBundle

class FragmentStackHost(
    initialKey: Any,
    private val aheadOfTimeBackCallbackRegistry: AheadOfTimeBackCallbackRegistry,
) : Bundleable, ScopedServices.Registered {
    var isActiveForBack: Boolean = false
        set(value) {
            field = value
            backCallback.isEnabled = value && backstackWillHandleBack
        }

    private var backstackWillHandleBack = false
        set(value) {
            field = value
            backCallback.isEnabled = isActiveForBack && value
        }

    val backstack = Backstack()

    private val backCallback = object : AheadOfTimeBackCallback(false) {
        override fun onBackReceived() {
            backstack.goBack()
        }
    }

    private val willHandleBackChangedListener = AheadOfTimeWillHandleBackChangedListener {
        backstackWillHandleBack = it
    }

    init {
        backstack.setBackHandlingModel(BackHandlingModel.AHEAD_OF_TIME)
        backstack.setup(History.of(initialKey))

        backstackWillHandleBack = backstack.willHandleAheadOfTimeBack()
        backstack.addAheadOfTimeWillHandleBackChangedListener(willHandleBackChangedListener)
    }

    override fun toBundle(): StateBundle = StateBundle().apply {
        putParcelable("BACKSTACK_STATE", backstack.toBundle())
    }

    override fun fromBundle(bundle: StateBundle?) {
        bundle?.run {
            backstack.fromBundle(getParcelable("BACKSTACK_STATE"))
        }
    }

    override fun onServiceRegistered() {
        aheadOfTimeBackCallbackRegistry.registerAheadOfTimeBackCallback(backCallback)
    }

    override fun onServiceUnregistered() {
        aheadOfTimeBackCallbackRegistry.unregisterAheadOfTimeBackCallback(backCallback)
    }
}