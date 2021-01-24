package com.zhuinden.simplestackbottomnavfragmentexample.features.root.third

import androidx.fragment.app.Fragment
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
class ThirdScreen : DefaultFragmentKey() {
    override fun instantiateFragment(): Fragment = ThirdFragment()
}