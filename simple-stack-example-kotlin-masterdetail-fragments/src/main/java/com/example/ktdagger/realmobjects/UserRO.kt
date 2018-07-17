package com.example.ktdagger.realmobjects

import android.os.Parcelable
import com.example.ktdagger.reponses.UserProfile
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Parcelize
open class UserRO(
    @PrimaryKey
    open var userId: Long? = null,
    @Index
    open var userName: String? = null,
    open var userEmail: String? = null,
    open var userPhoneNumber: String? = null,
    open var userPhoneNumberType: String? = null
) : RealmObject(), Parcelable {

    companion object {
        fun createFromUser(userProfile: UserProfile): UserRO {
            return UserRO(userProfile.id, userProfile.name, userProfile.email,
                userProfile.phoneNumber.number, userProfile.phoneNumber.type)
        }
    }
}