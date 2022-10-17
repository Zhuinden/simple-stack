package com.zhuinden.simplestackextensionscomposesample.features.registration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rxjava2.subscribeAsState
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestackextensions.servicesktx.lookup
import com.zhuinden.simplestackextensionscomposesample.core.ComposeFragment
import com.zhuinden.simplestackextensionscomposesample.utils.set

class EnterProfileDataFragment : ComposeFragment() {
    @Composable
    override fun FragmentComposable(backstack: Backstack) {
        val registrationViewModel = remember { backstack.lookup<RegistrationViewModel>() }

        val fullName = registrationViewModel.fullName.subscribeAsState(initial = "")
        val bio = registrationViewModel.bio.subscribeAsState(initial = "")

        val isEnabled = registrationViewModel.isEnterProfileNextEnabled.subscribeAsState(initial = false)

        EnterProfileDataScreenLayout(
            fullName = fullName,
            bio = bio,
            fullNameUpdater = registrationViewModel.fullName::set,
            bioUpdater = registrationViewModel.bio::set,
            onButtonClicked = registrationViewModel::onEnterProfileNextClicked,
            isButtonEnabled = isEnabled,
        )
    }
}