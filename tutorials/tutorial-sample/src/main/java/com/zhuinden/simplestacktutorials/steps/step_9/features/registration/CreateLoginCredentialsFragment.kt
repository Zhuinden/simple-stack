package com.zhuinden.simplestacktutorials.steps.step_9.features.registration

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.databinding.Step9CreateLoginCredentialsFragmentBinding
import com.zhuinden.simplestacktutorials.steps.step_9.utils.get
import com.zhuinden.simplestacktutorials.steps.step_9.utils.set
import com.zhuinden.simplestacktutorials.utils.onClick
import com.zhuinden.simplestacktutorials.utils.onTextChanged
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

class CreateLoginCredentialsFragment : KeyedFragment(R.layout.step9_create_login_credentials_fragment) {
    private val viewModel by lazy { lookup<RegistrationViewModel>() }

    private val compositeDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = Step9CreateLoginCredentialsFragmentBinding.bind(view)

        binding.textUsername.setText(viewModel.username.get())
        binding.textPassword.setText(viewModel.password.get())

        viewModel.isRegisterAndLoginEnabled.distinctUntilChanged().subscribeBy { enabled ->
            binding.buttonRegisterAndLogin.isEnabled = enabled
        }.addTo(compositeDisposable)

        binding.textUsername.onTextChanged { username -> viewModel.username.set(username) }
        binding.textPassword.onTextChanged { password -> viewModel.password.set(password) }
        binding.buttonRegisterAndLogin.onClick { viewModel.onRegisterAndLoginClicked() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }
}