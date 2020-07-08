package com.zhuinden.simplestacktutorials.steps.step_7.features.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.steps.step_7.core.navigation.BaseFragment
import com.zhuinden.simplestacktutorials.steps.step_7.core.viewmodels.lookup
import com.zhuinden.simplestacktutorials.utils.onClick
import com.zhuinden.simplestacktutorials.utils.onTextChanged
import kotlinx.android.synthetic.main.step7_enter_profile_data_fragment.*

class EnterProfileDataFragment : BaseFragment() {
    private val viewModel by lazy { lookup<RegistrationViewModel>() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.step7_enter_profile_data_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textFullName.setText(viewModel.fullName)
        textBio.setText(viewModel.bio)

        textFullName.onTextChanged { fullName -> viewModel.onFullNameChanged(fullName) }
        textBio.onTextChanged { bio -> viewModel.onBioChanged(bio) }
        buttonEnterProfileNext.onClick { viewModel.onEnterProfileNextClicked() }
    }
}