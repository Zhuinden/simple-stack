package com.zhuinden.simplestacktutorials.steps.step_8.features.main

import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestacktutorials.steps.step_8.core.navigation.FragmentKey
import com.zhuinden.simplestacktutorials.steps.step_8.core.viewmodels.HasServices
import com.zhuinden.simplestacktutorials.steps.step_8.core.viewmodels.add
import com.zhuinden.simplestacktutorials.steps.step_8.core.viewmodels.bindAs
import com.zhuinden.simplestacktutorials.steps.step_8.core.viewmodels.get
import com.zhuinden.simplestacktutorials.steps.step_8.features.form.FormViewModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MainKey(private val placeholder: String = ""): FragmentKey(), HasServices {
    override fun instantiateFragment(): Fragment = MainFragment()

    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(MainViewModel())
            bindAs<FormViewModel.ResultHandler>(get<MainViewModel>())
        }
    }
}