package com.community.simplestackkotlindaggerexample.data.api

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AllUsersResponse(
    val result: List<UserProfileResponse>
) : Parcelable

