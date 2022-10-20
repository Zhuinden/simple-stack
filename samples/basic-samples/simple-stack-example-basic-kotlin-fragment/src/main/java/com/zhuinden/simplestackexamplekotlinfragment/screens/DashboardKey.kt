package com.zhuinden.simplestackexamplekotlinfragment.screens

import androidx.fragment.app.Fragment
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import kotlinx.parcelize.Parcelize

/**
 * Created by Owner on 2017.11.13.
 */
@Parcelize
data object DashboardKey : DefaultFragmentKey() { // generate reliable `toString()` for no-args data class
    override fun instantiateFragment(): Fragment = DashboardFragment()
}
