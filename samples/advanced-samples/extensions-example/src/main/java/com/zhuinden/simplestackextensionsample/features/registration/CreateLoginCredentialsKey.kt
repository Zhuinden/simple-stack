package com.zhuinden.simplestackextensionsample.features.registration

import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ScopeKey
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
data object CreateLoginCredentialsKey : DefaultFragmentKey(), ScopeKey.Child {
    override fun instantiateFragment(): Fragment = CreateLoginCredentialsFragment()

    override fun getParentScopes(): List<String> = listOf(RegistrationViewModel::class.java.name)
}