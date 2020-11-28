package com.zhuinden.simplestacktutorials.steps.step_7.features.registration

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.databinding.Step7CreateLoginCredentialsFragmentBinding
import com.zhuinden.simplestacktutorials.utils.onClick
import com.zhuinden.simplestacktutorials.utils.onTextChanged

class CreateLoginCredentialsFragment : KeyedFragment(R.layout.step7_create_login_credentials_fragment) {
    private val viewModel by lazy { lookup<RegistrationViewModel>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = Step7CreateLoginCredentialsFragmentBinding.bind(view)

        binding.textUsername.setText(viewModel.username)
        binding.textPassword.setText(viewModel.password)

        binding.textUsername.onTextChanged { username -> viewModel.onUsernameChanged(username) }
        binding.textPassword.onTextChanged { password -> viewModel.onPasswordChanged(password) }
        binding.buttonRegisterAndLogin.onClick { viewModel.onRegisterAndLoginClicked() }
    }
}