package com.zhuinden.simplestackdemomultistack.features.main.mail

import com.zhuinden.simplestackdemomultistack.application.MainActivity
import com.zhuinden.simplestackdemomultistack.core.navigation.BaseFragment
import com.zhuinden.simplestackdemomultistack.core.navigation.MultistackFragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
data object MailKey : MultistackFragmentKey() {
    override fun stackIdentifier(): String = MainActivity.StackType.MAIL.name

    override fun createFragment(): BaseFragment = MailFragment()
}
