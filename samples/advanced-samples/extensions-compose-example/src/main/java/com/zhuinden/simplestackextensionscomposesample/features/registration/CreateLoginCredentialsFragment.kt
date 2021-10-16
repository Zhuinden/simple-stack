package com.zhuinden.simplestackextensionscomposesample.features.registration

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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestackextensions.servicesktx.lookup
import com.zhuinden.simplestackextensionscomposesample.core.ComposeFragment
import com.zhuinden.simplestackextensionscomposesample.utils.set

class CreateLoginCredentialsFragment : ComposeFragment() {
    @Composable
    override fun FragmentComposable(backstack: Backstack) {
        val registrationViewModel = remember { backstack.lookup<RegistrationViewModel>() }

        val username by registrationViewModel.username.subscribeAsState(initial = "")
        val password by registrationViewModel.password.subscribeAsState(initial = "")

        val isEnabled by registrationViewModel.isRegisterAndLoginEnabled.subscribeAsState(initial = false)

        ActualScreen(
            username = username,
            usernameUpdater = registrationViewModel.username::set,
            password = password,
            passwordUpdater = registrationViewModel.password::set,
            isButtonEnabled = isEnabled,
            onButtonClicked = registrationViewModel::onRegisterAndLoginClicked
        )
    }

    @Composable
    fun ActualScreen(
        username: String,
        usernameUpdater: (String) -> Unit,
        password: String,
        passwordUpdater: (String) -> Unit,
        isButtonEnabled: Boolean,
        onButtonClicked: () -> Unit,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = username,
                singleLine = true,
                placeholder = { Text("Username") },
                onValueChange = { username ->
                    usernameUpdater(username)
                })

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = password,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                placeholder = { Text("Password") },
                onValueChange = { password ->
                    passwordUpdater(password)
                })

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onButtonClicked, enabled = isButtonEnabled) {
                Text(text = "Register and login")
            }
        }
    }

    @Preview
    @Composable
    fun Preview() {
        ActualScreen(
            username = "Username",
            usernameUpdater = {},
            password = "password",
            passwordUpdater = {},
            isButtonEnabled = true,
            onButtonClicked = {},
        )
    }
}