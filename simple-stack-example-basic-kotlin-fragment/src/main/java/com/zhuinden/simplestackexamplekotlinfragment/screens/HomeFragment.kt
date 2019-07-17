package com.zhuinden.simplestackexamplekotlinfragment.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhuinden.simplestackexamplekotlinfragment.R
import com.zhuinden.simplestackexamplekotlinfragment.core.navigation.BaseFragment
import com.zhuinden.utils.backstack
import com.zhuinden.utils.onClick

import kotlinx.android.synthetic.main.home_view.*

/**
 * Created by Owner on 2017.11.13.
 */

class HomeFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.home_view, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeButton.onClick {
            backstack.goTo(OtherKey())
        }

        val homeKey = getKey<HomeKey>() // args
    }
}
