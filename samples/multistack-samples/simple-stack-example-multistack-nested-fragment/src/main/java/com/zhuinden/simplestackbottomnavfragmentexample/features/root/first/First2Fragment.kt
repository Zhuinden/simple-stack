package com.zhuinden.simplestackbottomnavfragmentexample.features.root.first

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.zhuinden.simplestackbottomnavfragmentexample.R
import com.zhuinden.simplestackbottomnavfragmentexample.core.navigation.FragmentStackHost
import com.zhuinden.simplestackbottomnavfragmentexample.databinding.FirstFragment2Binding
import com.zhuinden.simplestackbottomnavfragmentexample.features.root.RootScreen
import com.zhuinden.simplestackextensions.fragmentsktx.lookup

class First2Fragment : Fragment(R.layout.first_fragment_2) {
    private val localStack by lazy { lookup<FragmentStackHost>(RootScreen.FIRST_STACK).backstack }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FirstFragment2Binding.bind(view)

        binding.buttonFirstFragment2GoBack.setOnClickListener {
            localStack.goBack()
        }
    }
}