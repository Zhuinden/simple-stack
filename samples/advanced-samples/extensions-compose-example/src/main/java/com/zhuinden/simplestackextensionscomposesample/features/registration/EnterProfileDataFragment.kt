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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestackextensions.servicesktx.lookup
import com.zhuinden.simplestackextensionscomposesample.core.ComposeFragment
import com.zhuinden.simplestackextensionscomposesample.utils.set

class EnterProfileDataFragment : ComposeFragment() {
    @Composable
    override fun FragmentComposable(backstack: Backstack) {
        val registrationViewModel = remember { backstack.lookup<RegistrationViewModel>() }

        val fullName by registrationViewModel.fullName.subscribeAsState(initial = "")
        val bio by registrationViewModel.bio.subscribeAsState(initial = "")

        val isEnabled by registrationViewModel.isEnterProfileNextEnabled.subscribeAsState(initial = false)

        ActualScreen(
            fullName = fullName,
            bio = bio,
            fullNameUpdater = registrationViewModel.fullName::set,
            bioUpdater = registrationViewModel.bio::set,
            onButtonClicked = registrationViewModel::onEnterProfileNextClicked,
            isButtonEnabled = isEnabled,
        )
    }

    @Composable
    fun ActualScreen(
        fullName: String,
        fullNameUpdater: (String) -> Unit,
        bio: String,
        bioUpdater: (String) -> Unit,
        onButtonClicked: () -> Unit,
        isButtonEnabled: Boolean,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = fullName,
                singleLine = true,
                placeholder = { Text("Full name") },
                onValueChange = { value ->
                    fullNameUpdater(value)
                })

            Spacer(modifier = Modifier.height(8.dp))

            TextField(value = bio,
                singleLine = true,
                placeholder = { Text("Bio") },
                onValueChange = { value ->
                    bioUpdater(value)
                })

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onButtonClicked, enabled = isButtonEnabled) {
                Text(text = "Next")
            }
        }
    }

    @Preview
    @Composable
    fun Preview() {
        ActualScreen(
            fullName = "Full Name",
            fullNameUpdater = {},
            bio = "Bio",
            bioUpdater = {},
            onButtonClicked = {},
            isButtonEnabled = true,
        )
    }
}