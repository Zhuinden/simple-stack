package com.zhuinden.simplestacktutorials.steps.step_8.features.main

import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.get
import com.zhuinden.simplestackextensions.servicesktx.rebind
import com.zhuinden.simplestacktutorials.steps.step_8.core.navigation.FragmentKey
import com.zhuinden.simplestacktutorials.steps.step_8.features.form.FormViewModel
import kotlinx.parcelize.Parcelize

@Parcelize
data object MainKey : FragmentKey() {
    override fun instantiateFragment(): Fragment = MainFragment()

    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            val mainViewModel = MainViewModel()

            add(mainViewModel)
            rebind<FormViewModel.ResultHandler>(mainViewModel)
        }
    }

    operator fun invoke() = this
}