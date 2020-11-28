package com.zhuinden.simplestackextensionsample.features.registration

import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ScopeKey
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class EnterProfileDataKey(private val placeholder: String = "") : DefaultFragmentKey(), ScopeKey.Child {
    override fun instantiateFragment(): Fragment = EnterProfileDataFragment()

    override fun getParentScopes(): List<String> = listOf("registration")
}