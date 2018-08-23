package com.zhuinden.simplestackkotlindaggerexample.reponses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AllUsersResponse(
    val result: List<UserProfile>
) : Parcelable

