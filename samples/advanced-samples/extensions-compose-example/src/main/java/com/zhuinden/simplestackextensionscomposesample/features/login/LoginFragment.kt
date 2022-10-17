package com.zhuinden.simplestackextensionscomposesample.features.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rxjava2.subscribeAsState
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestackextensions.servicesktx.lookup
import com.zhuinden.simplestackextensionscomposesample.core.ComposeFragment
import com.zhuinden.simplestackextensionscomposesample.features.login.layouts.LoginScreenLayout
import com.zhuinden.simplestackextensionscomposesample.utils.set

class LoginFragment : ComposeFragment() {
    @Composable
    override fun FragmentComposable(backstack: Backstack) {
        val loginViewModel = remember { backstack.lookup<LoginViewModel>() }

        val username = loginViewModel.username.subscribeAsState(initial = "")
        val password = loginViewModel.password.subscribeAsState(initial = "")

        val isLoginEnabled = loginViewModel.isLoginEnabled.subscribeAsState(initial = false)

        LoginScreenLayout(
            username = username,
            password = password,
            usernameUpdater = loginViewModel.username::set,
            passwordUpdater = loginViewModel.password::set,
            onLoginClicked = loginViewModel::onLoginClicked,
            onRegisterClicked = loginViewModel::onRegisterClicked,
            isLoginEnabled = isLoginEnabled,
        )
    }
}