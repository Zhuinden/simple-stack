package com.zhuinden.simplestacktutorials.steps.step_9.features.registration

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.steps.step_9.core.navigation.BaseFragment
import com.zhuinden.simplestacktutorials.steps.step_9.core.viewmodels.lookup
import com.zhuinden.simplestacktutorials.steps.step_9.utils.get
import com.zhuinden.simplestacktutorials.steps.step_9.utils.set
import com.zhuinden.simplestacktutorials.utils.onClick
import com.zhuinden.simplestacktutorials.utils.onTextChanged
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.step9_enter_profile_data_fragment.*

class EnterProfileDataFragment : BaseFragment(R.layout.step9_enter_profile_data_fragment) {
    private val viewModel by lazy { lookup<RegistrationViewModel>() }

    private val compositeDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textFullName.setText(viewModel.fullName.get())
        textBio.setText(viewModel.bio.get())

        viewModel.isEnterProfileNextEnabled.distinctUntilChanged().subscribeBy { enabled ->
            buttonEnterProfileNext.isEnabled = enabled
        }.addTo(compositeDisposable)

        textFullName.onTextChanged { fullName -> viewModel.fullName.set(fullName) }
        textBio.onTextChanged { bio -> viewModel.bio.set(bio) }
        buttonEnterProfileNext.onClick { viewModel.onEnterProfileNextClicked() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }
}