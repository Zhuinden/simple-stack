package com.zhuinden.simplestackbottomnavfragmentexample.features.initial

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestackbottomnavfragmentexample.R
import com.zhuinden.simplestackbottomnavfragmentexample.databinding.InitialFragmentBinding
import com.zhuinden.simplestackbottomnavfragmentexample.features.root.RootScreen
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.backstack

class InitialFragment : KeyedFragment(R.layout.initial_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = InitialFragmentBinding.bind(view)

        binding.buttonInitialGoToNext.setOnClickListener {
            backstack.goTo(RootScreen())
        }
    }
}