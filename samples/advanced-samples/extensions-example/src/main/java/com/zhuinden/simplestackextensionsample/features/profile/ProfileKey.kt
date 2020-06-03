package com.zhuinden.simplestackextensionsample.features.profile

import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProfileKey(private val placeholder: String = "") : DefaultFragmentKey(), DefaultServiceProvider.HasServices {
    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(ProfileViewModel(lookup("appContext"), backstack))
        }
    }

    override fun getScopeTag(): String = javaClass.name

    override fun instantiateFragment(): Fragment = ProfileFragment()
}
