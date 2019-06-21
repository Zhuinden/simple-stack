package com.zhuinden.simplestackdemomultistack.features.main.mail

import com.zhuinden.simplestackdemomultistack.R
import com.zhuinden.simplestackdemomultistack.application.MainActivity
import com.zhuinden.simplestackdemomultistack.core.navigation.MultistackKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MailKey(private val placeholder: String = "") : MultistackKey() {
    override fun layout(): Int = R.layout.mail_view
    override fun stackIdentifier(): String = MainActivity.StackType.MAIL.name
}
