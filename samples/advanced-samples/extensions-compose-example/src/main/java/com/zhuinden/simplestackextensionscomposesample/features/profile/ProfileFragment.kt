package com.zhuinden.simplestackextensionscomposesample.features.profile

import androidx.compose.runtime.Composable
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestackextensionscomposesample.core.ComposeFragment

class ProfileFragment : ComposeFragment() {
    @Composable
    override fun FragmentComposable(backstack: Backstack) {
        val key = getKey<ProfileKey>()

        ProfileScreenLayout(
            username = key.username,
        )
    }
}