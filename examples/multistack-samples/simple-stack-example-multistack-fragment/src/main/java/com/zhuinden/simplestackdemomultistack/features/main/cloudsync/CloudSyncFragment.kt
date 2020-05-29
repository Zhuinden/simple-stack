package com.zhuinden.simplestackdemomultistack.features.main.cloudsync

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhuinden.simplestackdemomultistack.R
import com.zhuinden.simplestackdemomultistack.core.navigation.BaseFragment
import com.zhuinden.simplestackdemomultistack.core.navigation.backstack
import com.zhuinden.simplestackdemomultistack.features.main.cloudsync.another.AnotherKey
import com.zhuinden.simplestackdemomultistack.util.onClick
import kotlinx.android.synthetic.main.cloudsync_fragment.*

class CloudSyncFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.cloudsync_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonFirst.onClick {
            backstack.goTo(AnotherKey())
        }
    }
}
