package com.zhuinden.simplestacktutorials.steps.step_7.features.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.zhuinden.eventemitter.EventSource
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.backstack
import com.zhuinden.simplestackextensions.servicesktx.lookup
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.utils.onClick
import com.zhuinden.simplestacktutorials.utils.onTextChanged
import kotlinx.android.synthetic.main.step7_login_fragment.*

class LoginFragment : KeyedFragment(R.layout.step7_login_fragment) {
    private val viewModel by lazy { backstack.lookup<LoginViewModel>() }

    private var subscription: EventSource.NotificationToken? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textUsername.setText(viewModel.username)
        textPassword.setText(viewModel.password)

        textUsername.onTextChanged { username -> viewModel.onUsernameChanged(username) }
        textPassword.onTextChanged { password -> viewModel.onPasswordChanged(password) }
        buttonLogin.onClick { viewModel.onLoginClicked() }
        buttonRegister.onClick { viewModel.onRegisterClicked() }
    }

    override fun onStart() {
        super.onStart()

        subscription = viewModel.events.startListening { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onStop() {
        subscription?.stopListening()
        subscription = null

        super.onStop()
    }
}