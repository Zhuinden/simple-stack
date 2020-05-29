package com.community.simplestackkotlindaggerexample.screens.userdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.community.simplestackkotlindaggerexample.R
import com.community.simplestackkotlindaggerexample.core.navigation.BaseFragment
import com.community.simplestackkotlindaggerexample.data.database.User
import kotlinx.android.synthetic.main.fragment_user_detail.*


class UserDetailFragment : BaseFragment() {
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = getKey<UserDetailKey>().user
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_user_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = user

        textUsername.text = user.userName
        userPhoneNumber.text = "${user.userPhoneNumber} (${user.userPhoneNumberType})"
        userEmail.text = user.userEmail
    }
}
