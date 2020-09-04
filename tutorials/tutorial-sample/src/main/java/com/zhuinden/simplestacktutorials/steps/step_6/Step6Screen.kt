package com.zhuinden.simplestacktutorials.steps.step_6

import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey

abstract class Step6Screen : DefaultFragmentKey() {
    override fun getFragmentTag(): String = toString()
}