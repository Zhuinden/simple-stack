package com.zhuinden.simplestackdemomultistack.features.main.mail

import android.os.Parcelable

import com.zhuinden.simplestackdemomultistack.R
import com.zhuinden.simplestackdemomultistack.core.navigation.MultistackKey
import com.zhuinden.simplestackdemomultistack.application.MainActivity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MailKey(private val placeholder: String = "") : MultistackKey() {
    override fun layout(): Int = R.layout.mail_view
    override fun stackIdentifier(): String = MainActivity.StackType.MAIL.name

    companion object {
        fun create(): Parcelable = MailKey()
    }
}
