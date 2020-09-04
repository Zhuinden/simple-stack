package com.zhuinden.simplestacktutorials.steps.step_7.features.registration

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.utils.onClick
import com.zhuinden.simplestacktutorials.utils.onTextChanged
import kotlinx.android.synthetic.main.step7_create_login_credentials_fragment.*

class CreateLoginCredentialsFragment : KeyedFragment(R.layout.step7_create_login_credentials_fragment) {
    private val viewModel by lazy { lookup<RegistrationViewModel>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textUsername.setText(viewModel.username)
        textPassword.setText(viewModel.password)

        textUsername.onTextChanged { username -> viewModel.onUsernameChanged(username) }
        textPassword.onTextChanged { password -> viewModel.onPasswordChanged(password) }
        buttonRegisterAndLogin.onClick { viewModel.onRegisterAndLoginClicked() }
    }
}