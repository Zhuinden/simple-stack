package com.community.simplestackkotlindaggerexample.data.database

import android.os.Parcelable
import com.community.simplestackkotlindaggerexample.data.api.UserProfileResponse
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
open class User(
    @PrimaryKey
    open var userId: Long? = null,
    @Index
    open var userName: String? = null,
    open var userEmail: String? = null,
    open var userPhoneNumber: String? = null,
    open var userPhoneNumberType: String? = null
) : RealmObject(), Parcelable {

    companion object {
        fun createFromUser(userProfile: UserProfileResponse): User =
            User(userProfile.id, userProfile.name, userProfile.email,
                userProfile.phoneNumber.number, userProfile.phoneNumber.type)
    }
}