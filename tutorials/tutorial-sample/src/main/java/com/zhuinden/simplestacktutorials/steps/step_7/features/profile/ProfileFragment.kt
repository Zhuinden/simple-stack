package com.zhuinden.simplestacktutorials.steps.step_7.features.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.steps.step_7.core.navigation.BaseFragment

class ProfileFragment: BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.step7_profile_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Toast.makeText(requireContext(), "Welcome!", Toast.LENGTH_LONG).show()
    }
}