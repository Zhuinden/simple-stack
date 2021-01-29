package com.zhuinden.simplestackdemomultistack.features.main.cloudsync

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestackdemomultistack.R
import com.zhuinden.simplestackdemomultistack.core.navigation.BaseFragment
import com.zhuinden.simplestackdemomultistack.core.navigation.backstack
import com.zhuinden.simplestackdemomultistack.databinding.CloudsyncFragmentBinding
import com.zhuinden.simplestackdemomultistack.features.main.cloudsync.another.AnotherKey
import com.zhuinden.simplestackdemomultistack.util.onClick

class CloudSyncFragment : BaseFragment(R.layout.cloudsync_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = CloudsyncFragmentBinding.bind(view)

        binding.buttonFirst.onClick {
            backstack.goTo(AnotherKey())
        }
    }
}
