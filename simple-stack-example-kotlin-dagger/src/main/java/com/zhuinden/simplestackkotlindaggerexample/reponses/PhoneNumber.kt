package com.zhuinden.simplestackkotlindaggerexample.reponses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PhoneNumber (
    val number: String,
    val type: String
) : Parcelable

