package com.example.ktdagger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ktdagger.userdetail.UsersKey
import kotlinx.android.synthetic.main.fragment_home.loadUsersButton
import org.jetbrains.anko.sdk15.listeners.onClick

class HomeFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUsersButton.onClick { MainActivity[view.context].navigateTo(UsersKey()); }
    }
}