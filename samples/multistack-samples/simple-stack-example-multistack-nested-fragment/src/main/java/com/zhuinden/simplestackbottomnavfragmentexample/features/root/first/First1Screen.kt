package com.zhuinden.simplestackbottomnavfragmentexample.features.root.first

import androidx.fragment.app.Fragment
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
class First1Screen : DefaultFragmentKey() {
    override fun instantiateFragment(): Fragment = First1Fragment()
}