package com.zhuinden.simplestacktutorials.steps.step_7.features.profile

import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestacktutorials.steps.step_7.features.core.FragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileKey(private val placeholder: String = "") : FragmentKey() {
    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(ProfileViewModel(lookupService("appContext"), backstack))
        }
    }

    override fun instantiateFragment(): Fragment = ProfileFragment()
}
