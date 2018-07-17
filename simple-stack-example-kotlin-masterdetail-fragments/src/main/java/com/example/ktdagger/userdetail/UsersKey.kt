package com.example.ktdagger.userdetail

import com.example.ktdagger.BaseKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UsersKey(val tag: String) : BaseKey() {
    constructor() : this("UsersKey")

    override fun createFragment() = UsersFragment()
}