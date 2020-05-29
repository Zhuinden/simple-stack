package com.zhuinden.simplestacktutorials.steps.step_8.features.form

import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.statebundle.StateBundle

class FormViewModel(
    private val resultHandler: ResultHandler,
    private val backstack: Backstack
): Bundleable {
    interface ResultHandler {
        fun handleResult(someData: String, moreData: String)
    }

    var someData: String = ""
    var moreData: String = ""

    fun onButtonClicked() {
        resultHandler.handleResult(someData, moreData)
        backstack.goBack()
    }

    override fun toBundle(): StateBundle = StateBundle().apply {
        putString("someData", someData)
        putString("moreData", moreData)
    }

    override fun fromBundle(bundle: StateBundle?) {
        bundle?.run {
            someData = getString("someData", "")
            moreData = getString("moreData", "")
        }
    }
}