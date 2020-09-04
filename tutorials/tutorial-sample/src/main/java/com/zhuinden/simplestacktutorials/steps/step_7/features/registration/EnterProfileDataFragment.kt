package com.zhuinden.simplestacktutorials.steps.step_7.features.registration

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.utils.onClick
import com.zhuinden.simplestacktutorials.utils.onTextChanged
import kotlinx.android.synthetic.main.step7_enter_profile_data_fragment.*

class EnterProfileDataFragment : KeyedFragment(R.layout.step7_enter_profile_data_fragment) {
    private val viewModel by lazy { lookup<RegistrationViewModel>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textFullName.setText(viewModel.fullName)
        textBio.setText(viewModel.bio)

        textFullName.onTextChanged { fullName -> viewModel.onFullNameChanged(fullName) }
        textBio.onTextChanged { bio -> viewModel.onBioChanged(bio) }
        buttonEnterProfileNext.onClick { viewModel.onEnterProfileNextClicked() }
    }
}