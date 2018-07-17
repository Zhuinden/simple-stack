package com.example.ktdagger.userdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ktdagger.BaseFragment
import com.example.ktdagger.R
import com.example.ktdagger.realmobjects.UserRO
import kotlinx.android.synthetic.main.fragment_user_detail.userEmail
import kotlinx.android.synthetic.main.fragment_user_detail.userPhoneNumber
import kotlinx.android.synthetic.main.view_item_contact.userName

private const val ARG_USERO = "useRO"

class UserDetailFragment : BaseFragment() {

    private var userRO: UserRO? = null

    companion object {
        @JvmStatic
        fun newInstance(userRO: UserRO) =
            UserDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_USERO, userRO)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userRO = it.getParcelable(ARG_USERO)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_detail, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (userRO != null) {
            userName.text = userRO!!.userName
            userPhoneNumber.text = userRO!!.userPhoneNumber + " (" + userRO!!
                .userPhoneNumberType + ")"
            userEmail.text = userRO!!.userEmail
        }
    }

}
