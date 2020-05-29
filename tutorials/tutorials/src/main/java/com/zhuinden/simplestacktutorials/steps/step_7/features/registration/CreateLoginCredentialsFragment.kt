package com.zhuinden.simplestacktutorials.steps.step_7.features.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.steps.step_7.core.navigation.BaseFragment
import com.zhuinden.simplestacktutorials.steps.step_7.core.viewmodels.lookup
import com.zhuinden.simplestacktutorials.utils.onClick
import com.zhuinden.simplestacktutorials.utils.onTextChanged
import kotlinx.android.synthetic.main.step7_create_login_credentials_fragment.*

class CreateLoginCredentialsFragment: BaseFragment() {
    private val viewModel by lazy { lookup<RegistrationViewModel>() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.step7_create_login_credentials_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textUsername.setText(viewModel.username)
        textPassword.setText(viewModel.password)

        textUsername.onTextChanged { username -> viewModel.onUsernameChanged(username) }
        textPassword.onTextChanged { password -> viewModel.onPasswordChanged(password) }
        buttonRegisterAndLogin.onClick { viewModel.onRegisterAndLoginClicked() }
    }
}