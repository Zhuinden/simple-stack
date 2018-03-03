package com.zhuinden.simplestackexamplekotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import kotlinx.android.synthetic.main.home_view.*
import org.jetbrains.anko.sdk15.listeners.onClick

/**
 * Created by Owner on 2017.11.13.
 */

class HomeFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.home_view, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeButton.onClick {
            MainActivity.get(view.context).navigateTo(OtherKey())
        }

        val homeKey = getKey<HomeKey>()
    }
}
