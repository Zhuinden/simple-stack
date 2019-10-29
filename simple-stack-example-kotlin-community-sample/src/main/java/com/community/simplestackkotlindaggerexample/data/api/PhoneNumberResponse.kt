package com.community.simplestackkotlindaggerexample.data.api

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PhoneNumberResponse(
    val number: String,
    val type: String
) : Parcelable

