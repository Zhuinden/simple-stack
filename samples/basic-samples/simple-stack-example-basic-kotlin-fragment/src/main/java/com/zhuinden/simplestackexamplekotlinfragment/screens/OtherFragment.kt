package com.zhuinden.simplestackexamplekotlinfragment.screens

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestackexamplekotlinfragment.R
import com.zhuinden.simplestackextensions.fragments.KeyedFragment


/**
 * Created by Owner on 2017.11.13.
 */

class OtherFragment : KeyedFragment(R.layout.other_view) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ...
    }
}
