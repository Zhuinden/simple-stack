package com.zhuinden.simplestackextensionscomposesample.features.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestackextensionscomposesample.core.ComposeFragment

class ProfileFragment : ComposeFragment() {
    @Composable
    override fun FragmentComposable(backstack: Backstack) {
        val key = getKey<ProfileKey>()

        ActualScreen(
            username = key.username,
        )
    }

    @Composable
    fun ActualScreen(
        username: String,
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Hello $username!")
        }
    }

    @Preview
    @Composable
    fun Preview() {
        ActualScreen(
            username = "username",
        )
    }
}