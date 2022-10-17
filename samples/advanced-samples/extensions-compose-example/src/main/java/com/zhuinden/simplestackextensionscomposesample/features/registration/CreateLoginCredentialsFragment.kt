package com.zhuinden.simplestackextensionscomposesample.features.registration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rxjava2.subscribeAsState
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestackextensions.servicesktx.lookup
import com.zhuinden.simplestackextensionscomposesample.core.ComposeFragment
import com.zhuinden.simplestackextensionscomposesample.utils.set

class CreateLoginCredentialsFragment : ComposeFragment() {
    @Composable
    override fun FragmentComposable(backstack: Backstack) {
        val registrationViewModel = remember { backstack.lookup<RegistrationViewModel>() }

        val username = registrationViewModel.username.subscribeAsState(initial = "")
        val password = registrationViewModel.password.subscribeAsState(initial = "")

        val isEnabled = registrationViewModel.isRegisterAndLoginEnabled.subscribeAsState(initial = false)

        CreateLoginCredentialsScreenLayout(
            username = username,
            usernameUpdater = registrationViewModel.username::set,
            password = password,
            passwordUpdater = registrationViewModel.password::set,
            isButtonEnabled = isEnabled,
            onButtonClicked = registrationViewModel::onRegisterAndLoginClicked
        )
    }
}