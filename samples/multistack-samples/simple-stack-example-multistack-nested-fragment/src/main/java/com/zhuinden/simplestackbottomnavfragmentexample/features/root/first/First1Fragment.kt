package com.zhuinden.simplestackbottomnavfragmentexample.features.root.first

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.zhuinden.simplestackbottomnavfragmentexample.R
import com.zhuinden.simplestackbottomnavfragmentexample.core.navigation.FragmentStackHost
import com.zhuinden.simplestackbottomnavfragmentexample.databinding.FirstFragment1Binding
import com.zhuinden.simplestackbottomnavfragmentexample.features.root.RootScreen
import com.zhuinden.simplestackextensions.fragmentsktx.lookup

class First1Fragment : Fragment(R.layout.first_fragment_1) {
    private val localStack by lazy { lookup<FragmentStackHost>(RootScreen.FIRST_STACK).backstack }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FirstFragment1Binding.bind(view)

        binding.buttonFirstFragment1GoTo2.setOnClickListener {
            localStack.goTo(First2Screen())
        }
    }
}