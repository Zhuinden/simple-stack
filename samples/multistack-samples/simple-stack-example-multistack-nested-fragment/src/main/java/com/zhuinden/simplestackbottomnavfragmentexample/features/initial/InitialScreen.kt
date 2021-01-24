package com.zhuinden.simplestackbottomnavfragmentexample.features.initial

import androidx.fragment.app.Fragment
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
class InitialScreen : DefaultFragmentKey() {
    override fun instantiateFragment(): Fragment = InitialFragment()
}