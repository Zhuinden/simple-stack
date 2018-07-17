package com.zhuinden.simplestackkotlindaggerexample.userdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhuinden.simplestackkotlindaggerexample.BaseFragment
import com.zhuinden.simplestackkotlindaggerexample.R
import com.zhuinden.simplestackkotlindaggerexample.realmobjects.UserRO
import com.zhuinden.simplestackkotlindaggerexample.requireArguments
import kotlinx.android.synthetic.main.fragment_user_detail.userEmail
import kotlinx.android.synthetic.main.fragment_user_detail.userName
import kotlinx.android.synthetic.main.fragment_user_detail.userPhoneNumber


class UserDetailFragment : BaseFragment() {

    private lateinit var userRO: UserRO

    companion object {
        private const val ARG_USER_RO = "userRO"
        @JvmStatic
        fun newInstance(userRO: UserRO) =
            UserDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_USER_RO, userRO)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments.let {
            userRO = it.getParcelable(ARG_USER_RO)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_detail, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userRO = userRO
        userName.text = userRO.userName
        userPhoneNumber.text = userRO.userPhoneNumber + " (" + userRO.userPhoneNumberType + ")"
        userEmail.text = userRO.userEmail
    }

}
