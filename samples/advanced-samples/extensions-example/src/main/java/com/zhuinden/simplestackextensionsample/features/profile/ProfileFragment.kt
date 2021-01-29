package com.zhuinden.simplestackextensionsample.features.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensionsample.R
import com.zhuinden.simplestackextensionsample.utils.showToast

class ProfileFragment : KeyedFragment(R.layout.profile_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showToast("Welcome ${getKey<ProfileKey>().username}!", Toast.LENGTH_LONG)
    }
}