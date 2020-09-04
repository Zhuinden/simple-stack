package com.zhuinden.simplestacktutorials.steps.step_6

import android.os.Handler
import android.os.Looper
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.backstack
import com.zhuinden.simplestacktutorials.R

class Step6FirstFragment : KeyedFragment(R.layout.step6_first_fragment) {
    private val handler = Handler(Looper.getMainLooper())

    private var didNavigate = false

    private val runAfterDelay = Runnable {
        if (!didNavigate) {
            didNavigate = true
            backstack.setHistory(History.of(Step6SecondScreen()), StateChange.REPLACE) // <--
        }
    }

    override fun onStart() {
        super.onStart()
        handler.postDelayed(runAfterDelay, 1250L)
    }

    override fun onStop() {
        handler.removeCallbacksAndMessages(runAfterDelay)
        super.onStop()
    }
}
