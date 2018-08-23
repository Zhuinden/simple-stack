package com.zhuinden.simplestackkotlindaggerexample.userdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhuinden.simplestackkotlindaggerexample.BaseFragment
import com.zhuinden.simplestackkotlindaggerexample.R
import com.zhuinden.simplestackkotlindaggerexample.realmobjects.UserRO
import kotlinx.android.synthetic.main.fragment_user_detail.*


class UserDetailFragment : BaseFragment() {
    private lateinit var userRO: UserRO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userRO = getKey<UserDetailKey>().userRO
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_user_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userRO = userRO
        userName.text = userRO.userName
        userPhoneNumber.text = "${userRO.userPhoneNumber} (${userRO.userPhoneNumberType})"
        userEmail.text = userRO.userEmail
    }
}
