package com.zhuinden.simplestackdemomultistack.features.main.mail

import com.zhuinden.simplestackdemomultistack.R
import com.zhuinden.simplestackdemomultistack.application.MainActivity
import com.zhuinden.simplestackdemomultistack.core.navigation.MultistackViewKey
import kotlinx.parcelize.Parcelize

@Parcelize
data object MailKey : MultistackViewKey() {
    override fun layout(): Int = R.layout.mail_view
    override fun stackIdentifier(): String = MainActivity.StackType.MAIL.name
}
