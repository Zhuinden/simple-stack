package com.zhuinden.simplestacktutorials.steps.step_7.features.registration

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.databinding.Step7EnterProfileDataFragmentBinding
import com.zhuinden.simplestacktutorials.utils.onClick
import com.zhuinden.simplestacktutorials.utils.onTextChanged

class EnterProfileDataFragment : KeyedFragment(R.layout.step7_enter_profile_data_fragment) {
    private val viewModel by lazy { lookup<RegistrationViewModel>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = Step7EnterProfileDataFragmentBinding.bind(view)

        binding.textFullName.setText(viewModel.fullName)
        binding.textBio.setText(viewModel.bio)

        binding.textFullName.onTextChanged { fullName -> viewModel.onFullNameChanged(fullName) }
        binding.textBio.onTextChanged { bio -> viewModel.onBioChanged(bio) }
        binding.buttonEnterProfileNext.onClick { viewModel.onEnterProfileNextClicked() }
    }
}