package com.zhuinden.simplestackextensionscomposesample.features.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rxjava2.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestackextensions.servicesktx.lookup
import com.zhuinden.simplestackextensionscomposesample.core.ComposeFragment
import com.zhuinden.simplestackextensionscomposesample.utils.set

class LoginFragment : ComposeFragment() {
    @Composable
    override fun FragmentComposable(backstack: Backstack) {
        val loginViewModel = remember { backstack.lookup<LoginViewModel>() }

        val username by loginViewModel.username.subscribeAsState(initial = "")
        val password by loginViewModel.password.subscribeAsState(initial = "")

        val isLoginEnabled by loginViewModel.isLoginEnabled.subscribeAsState(initial = false)

        ActualScreen(
            username = username,
            password = password,
            usernameUpdater = loginViewModel.username::set,
            passwordUpdater = loginViewModel.password::set,
            onLoginClicked = loginViewModel::onLoginClicked,
            onRegisterClicked = loginViewModel::onRegisterClicked,
            isLoginEnabled = isLoginEnabled,
        )
    }

    @Composable
    fun ActualScreen(
        username: String,
        usernameUpdater: (String) -> Unit,
        password: String,
        passwordUpdater: (String) -> Unit,
        isLoginEnabled: Boolean,
        onLoginClicked: () -> Unit,
        onRegisterClicked: () -> Unit,
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(value = username, placeholder = { Text("Username") }, onValueChange = { value: String ->
                usernameUpdater(value)
            })

            Spacer(modifier = Modifier.height(8.dp))

            TextField(value = password, placeholder = { Text("Password") }, onValueChange = { value: String ->
                passwordUpdater(value)
            })

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onLoginClicked, enabled = isLoginEnabled) {
                Text(text = "LOGIN")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onRegisterClicked) {
                Text(text = "REGISTER")
            }
        }
    }

    @Preview
    @Composable
    fun Preview() {
        ActualScreen(
            username = "username",
            password = "password",
            usernameUpdater = {},
            passwordUpdater = {},
            isLoginEnabled = true,
            onLoginClicked = {},
            onRegisterClicked = {},
        )
    }
}