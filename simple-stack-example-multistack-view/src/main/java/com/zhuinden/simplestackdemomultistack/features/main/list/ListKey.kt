package com.zhuinden.simplestackdemomultistack.features.main.list

import android.os.Parcelable

import com.zhuinden.simplestackdemomultistack.R
import com.zhuinden.simplestackdemomultistack.core.navigation.MultistackKey
import com.zhuinden.simplestackdemomultistack.application.MainActivity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ListKey(private val placeholder: String = "") : MultistackKey() {
    override fun layout(): Int = R.layout.list_view
    override fun stackIdentifier(): String = MainActivity.StackType.LIST.name

    companion object {
        fun create(): Parcelable = ListKey()
    }
}

