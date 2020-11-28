package com.zhuinden.simplestackdemoexamplefragments.core.navigation

import android.os.Bundle
import android.view.*
import com.zhuinden.simplestack.KeyContextWrapper
import com.zhuinden.simplestackextensions.fragments.KeyedFragment

/**
 * Created by Zhuinden on 2017.01.26..
 */

abstract class BaseFragment() : KeyedFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = getKey<FragmentKey>().let { key ->
        LayoutInflater.from(KeyContextWrapper(inflater.context, key)).inflate(key.layout(), container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(getKey<FragmentKey>().menu(), menu)
    }
}
