package com.zhuinden.simplestackexamplekotlinfragment.screens

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestackexamplekotlinfragment.R
import com.zhuinden.simplestackexamplekotlinfragment.utils.onClick
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.backstack
import kotlinx.android.synthetic.main.home_view.*

/**
 * Created by Owner on 2017.11.13.
 */

class HomeFragment : KeyedFragment(R.layout.home_view) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeButton.onClick {
            backstack.goTo(OtherKey())
        }

        val homeKey = getKey<HomeKey>() // args
    }
}
