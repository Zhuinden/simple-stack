package com.zhuinden.simplestackdemomultistack.features.main.mail

import com.zhuinden.simplestackdemomultistack.application.MainActivity
import com.zhuinden.simplestackdemomultistack.core.navigation.BaseFragment
import com.zhuinden.simplestackdemomultistack.core.navigation.MultistackFragmentKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class MailKey(private val placeholder: String = "") : MultistackFragmentKey() {
    override fun stackIdentifier(): String = MainActivity.StackType.MAIL.name

    override fun createFragment(): BaseFragment = MailFragment()
}
