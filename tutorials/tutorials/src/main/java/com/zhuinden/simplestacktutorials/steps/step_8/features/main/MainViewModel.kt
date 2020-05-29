package com.zhuinden.simplestacktutorials.steps.step_8.features.main

import com.zhuinden.simplestack.Bundleable
import com.zhuinden.simplestacktutorials.steps.step_8.features.form.FormViewModel
import com.zhuinden.statebundle.StateBundle

class MainViewModel: FormViewModel.ResultHandler, Bundleable {
    var state: String = ""
        private set

    override fun handleResult(someData: String, moreData: String) {
        this.state = "$someData $moreData"
    }

    override fun toBundle(): StateBundle = StateBundle().apply {
        putString("state", state)
    }

    override fun fromBundle(bundle: StateBundle?) {
        bundle?.run {
            state = getString("state", "")
        }
    }
}