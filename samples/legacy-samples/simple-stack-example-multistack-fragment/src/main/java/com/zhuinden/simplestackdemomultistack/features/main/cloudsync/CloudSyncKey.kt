package com.zhuinden.simplestackdemomultistack.features.main.cloudsync

import com.zhuinden.simplestackdemomultistack.application.MainActivity
import com.zhuinden.simplestackdemomultistack.core.navigation.BaseFragment
import com.zhuinden.simplestackdemomultistack.core.navigation.MultistackFragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
data object CloudSyncKey : MultistackFragmentKey() {
    override fun stackIdentifier(): String = MainActivity.StackType.CLOUDSYNC.name

    override fun createFragment(): BaseFragment = CloudSyncFragment()
}
