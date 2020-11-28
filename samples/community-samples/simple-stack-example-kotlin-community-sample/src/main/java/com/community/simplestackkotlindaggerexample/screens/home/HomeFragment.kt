package com.community.simplestackkotlindaggerexample.screens.home

import android.os.Bundle
import android.view.View
import com.community.simplestackkotlindaggerexample.R
import com.community.simplestackkotlindaggerexample.databinding.FragmentHomeBinding
import com.community.simplestackkotlindaggerexample.screens.users.UsersKey
import com.community.simplestackkotlindaggerexample.utils.onClick
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.backstack

class HomeFragment : KeyedFragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentHomeBinding.bind(view)

        binding.buttonLoadUsers.onClick {
            backstack.goTo(UsersKey())
        }
    }
}