package com.community.simplestackkotlindaggerexample.screens.userdetail

import android.os.Bundle
import android.view.View
import com.community.simplestackkotlindaggerexample.R
import com.community.simplestackkotlindaggerexample.data.database.User
import com.community.simplestackkotlindaggerexample.databinding.FragmentUserDetailBinding
import com.zhuinden.simplestackextensions.fragments.KeyedFragment

class UserDetailFragment : KeyedFragment(R.layout.fragment_user_detail) {
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = getKey<UserDetailKey>().user
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = user

        val binding = FragmentUserDetailBinding.bind(view)

        binding.textUsername.text = user.userName
        binding.userPhoneNumber.text = "${user.userPhoneNumber} (${user.userPhoneNumberType})"
        binding.userEmail.text = user.userEmail
    }
}
