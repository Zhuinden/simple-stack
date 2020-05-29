package com.zhuinden.simplestacktutorials.steps.step_9.features.profile

import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestacktutorials.steps.step_9.core.navigation.FragmentKey
import com.zhuinden.simplestacktutorials.steps.step_9.core.viewmodels.HasServices
import com.zhuinden.simplestacktutorials.steps.step_9.core.viewmodels.add
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProfileKey(private val placeholder: String = "") : FragmentKey(), HasServices {
    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(ProfileViewModel(lookupService("appContext"), backstack))
        }
    }

    override fun instantiateFragment(): Fragment = ProfileFragment()
}
