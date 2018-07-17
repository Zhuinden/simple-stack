package com.example.ktdagger.userdetail

import com.example.ktdagger.BaseKey
import com.example.ktdagger.realmobjects.UserRO
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserDetailKey(
    val tag: String
) : BaseKey() {

    private lateinit var userRO: UserRO;

    constructor(userRO: UserRO) : this("UserDetailKey") {
        this.userRO = userRO;
    }

    override fun createFragment() = UserDetailFragment.newInstance(userRO)
}