package com.zhuinden.simplestacktutorials.steps.step_7.features.registration

import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ScopeKey
import com.zhuinden.simplestacktutorials.steps.step_7.features.core.FragmentKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CreateLoginCredentialsKey(private val noArg: String = "") : FragmentKey(), ScopeKey.Child {
    override fun instantiateFragment(): Fragment = CreateLoginCredentialsFragment()

    override fun getParentScopes(): List<String> = listOf("registration")
}