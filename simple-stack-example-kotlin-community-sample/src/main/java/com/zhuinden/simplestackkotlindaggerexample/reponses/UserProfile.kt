package com.zhuinden.simplestackkotlindaggerexample.reponses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserProfile(
    val id: Long,
    val name: String,
    val email: String,
    val phoneNumber: PhoneNumber
) : Parcelable