package com.zhuinden.simplestacktutorials.steps.step_9.features.registration

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.databinding.Step9EnterProfileDataFragmentBinding
import com.zhuinden.simplestacktutorials.steps.step_9.utils.get
import com.zhuinden.simplestacktutorials.steps.step_9.utils.set
import com.zhuinden.simplestacktutorials.utils.onClick
import com.zhuinden.simplestacktutorials.utils.onTextChanged
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

class EnterProfileDataFragment : KeyedFragment(R.layout.step9_enter_profile_data_fragment) {
    private val viewModel by lazy { lookup<RegistrationViewModel>() }

    private val compositeDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = Step9EnterProfileDataFragmentBinding.bind(view)

        binding.textFullName.setText(viewModel.fullName.get())
        binding.textBio.setText(viewModel.bio.get())

        viewModel.isEnterProfileNextEnabled.distinctUntilChanged().subscribeBy { enabled ->
            binding.buttonEnterProfileNext.isEnabled = enabled
        }.addTo(compositeDisposable)

        binding.textFullName.onTextChanged { fullName -> viewModel.fullName.set(fullName) }
        binding.textBio.onTextChanged { bio -> viewModel.bio.set(bio) }
        binding.buttonEnterProfileNext.onClick { viewModel.onEnterProfileNextClicked() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }
}