package com.zhuinden.simplestackextensionscomposesample.features.registration

import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ScopeKey
import com.zhuinden.simplestackextensionscomposesample.app.FragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateLoginCredentialsKey(
    private val noArgsPlaceholder: String = "",
) : FragmentKey(), ScopeKey.Child {
    override fun getParentScopes(): List<String> = listOf(RegistrationViewModel::class.java.name)

    override fun instantiateFragment(): Fragment = CreateLoginCredentialsFragment()
}