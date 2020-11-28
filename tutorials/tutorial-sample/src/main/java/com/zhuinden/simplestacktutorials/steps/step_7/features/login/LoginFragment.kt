package com.zhuinden.simplestacktutorials.steps.step_7.features.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.zhuinden.liveevent.observe
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.backstack
import com.zhuinden.simplestackextensions.servicesktx.lookup
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.databinding.Step7LoginFragmentBinding
import com.zhuinden.simplestacktutorials.utils.onClick
import com.zhuinden.simplestacktutorials.utils.onTextChanged

class LoginFragment : KeyedFragment(R.layout.step7_login_fragment) {
    private val viewModel by lazy { backstack.lookup<LoginViewModel>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = Step7LoginFragmentBinding.bind(view)

        binding.textUsername.setText(viewModel.username)
        binding.textPassword.setText(viewModel.password)

        binding.textUsername.onTextChanged { username -> viewModel.onUsernameChanged(username) }
        binding.textPassword.onTextChanged { password -> viewModel.onPasswordChanged(password) }
        binding.buttonLogin.onClick { viewModel.onLoginClicked() }
        binding.buttonRegister.onClick { viewModel.onRegisterClicked() }

        viewModel.events.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }
}