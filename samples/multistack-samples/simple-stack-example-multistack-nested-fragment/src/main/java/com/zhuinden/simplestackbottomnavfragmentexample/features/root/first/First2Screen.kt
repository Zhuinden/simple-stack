package com.zhuinden.simplestackbottomnavfragmentexample.features.root.first

import androidx.fragment.app.Fragment
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
class First2Screen : DefaultFragmentKey() {
    override fun instantiateFragment(): Fragment = First2Fragment()
}