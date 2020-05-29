package com.zhuinden.simplestacktutorials.steps.step_6

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestacktutorials.R

class Step6FirstFragment : Step6BaseFragment() {
    private val handler = Handler(Looper.getMainLooper())

    private var didNavigate = false

    private val runAfterDelay = Runnable {
        if (!didNavigate) {
            didNavigate = true
            backstack.setHistory(History.of(Step6SecondScreen()), StateChange.REPLACE) // <--
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.step6_first_fragment, container, false)

    override fun onStart() {
        super.onStart()
        handler.postDelayed(runAfterDelay, 1250L)
    }

    override fun onStop() {
        handler.removeCallbacksAndMessages(runAfterDelay)
        super.onStop()
    }
}
