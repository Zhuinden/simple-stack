package com.zhuinden.simplestackextensionscomposesample.features.registration

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun EnterProfileDataScreenLayout(
    fullName: State<String>,
    fullNameUpdater: (String) -> Unit,
    bio: State<String>,
    bioUpdater: (String) -> Unit,
    onButtonClicked: () -> Unit,
    isButtonEnabled: State<Boolean>,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = fullName.value,
            singleLine = true,
            placeholder = { Text("Full name") },
            onValueChange = fullNameUpdater,
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = bio.value,
            singleLine = true,
            placeholder = { Text("Bio") },
            onValueChange = bioUpdater,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onButtonClicked, enabled = isButtonEnabled.value) {
            Text(text = "Next")
        }
    }
}

@Preview
@Composable
fun PreviewEnterProfileDataLayout() {
    EnterProfileDataScreenLayout(
        fullName = mutableStateOf("Full Name"),
        fullNameUpdater = {},
        bio = mutableStateOf("Bio"),
        bioUpdater = {},
        onButtonClicked = {},
        isButtonEnabled = mutableStateOf(true),
    )
}
