package com.zhuinden.simplestackbottomnavfragmentexample.features.root.second

import androidx.fragment.app.Fragment
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
class SecondScreen : DefaultFragmentKey() {
    override fun instantiateFragment(): Fragment = SecondFragment()
}