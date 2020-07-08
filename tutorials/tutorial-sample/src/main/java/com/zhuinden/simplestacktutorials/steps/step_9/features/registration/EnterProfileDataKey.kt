package com.zhuinden.simplestacktutorials.steps.step_9.features.registration

import androidx.fragment.app.Fragment
import com.zhuinden.simplestack.ScopeKey
import com.zhuinden.simplestacktutorials.steps.step_9.core.navigation.FragmentKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EnterProfileDataKey(private val placeholder: String = "") : FragmentKey(), ScopeKey.Child {
    override fun instantiateFragment(): Fragment = EnterProfileDataFragment()

    override fun getParentScopes(): List<String> = listOf("registration")
}