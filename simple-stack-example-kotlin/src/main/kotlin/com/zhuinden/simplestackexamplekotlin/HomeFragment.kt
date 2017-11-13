package com.zhuinden.simplestackexamplekotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import butterknife.ButterKnife
import butterknife.OnClick

/**
 * Created by Owner on 2017. 06. 29..
 */

class HomeFragment : BaseFragment() {
    @OnClick(R.id.home_button)
    fun goToOtherView(view: View) {
        MainActivity.get(view.context).navigateTo(OtherKey)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.home_view, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)

        val homeKey = getKey<HomeKey>()
    }
}
