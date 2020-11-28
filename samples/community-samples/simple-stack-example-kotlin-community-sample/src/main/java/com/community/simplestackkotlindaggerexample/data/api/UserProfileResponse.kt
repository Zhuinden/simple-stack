package com.community.simplestackkotlindaggerexample.data.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserProfileResponse(
    val id: Long,
    val name: String,
    val email: String,
    val phoneNumber: PhoneNumberResponse
) : Parcelable