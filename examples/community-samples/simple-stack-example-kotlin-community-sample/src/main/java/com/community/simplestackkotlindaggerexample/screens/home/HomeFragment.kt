package com.community.simplestackkotlindaggerexample.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.community.simplestackkotlindaggerexample.R
import com.community.simplestackkotlindaggerexample.core.navigation.BaseFragment
import com.community.simplestackkotlindaggerexample.screens.users.UsersKey
import com.community.simplestackkotlindaggerexample.utils.onClick
import com.zhuinden.simplestack.navigator.Navigator
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonLoadUsers.onClick {
            Navigator.getBackstack(requireContext()).goTo(UsersKey())
        }
    }
}