package com.community.simplestackkotlindaggerexample.screens.userdetail

import android.os.Bundle
import android.view.View
import com.community.simplestackkotlindaggerexample.R
import com.community.simplestackkotlindaggerexample.data.database.User
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import kotlinx.android.synthetic.main.fragment_user_detail.*


class UserDetailFragment : KeyedFragment(R.layout.fragment_user_detail) {
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = getKey<UserDetailKey>().user
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = user

        textUsername.text = user.userName
        userPhoneNumber.text = "${user.userPhoneNumber} (${user.userPhoneNumberType})"
        userEmail.text = user.userEmail
    }
}
