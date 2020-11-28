package com.zhuinden.simplestacktutorials.steps.step_9.features.login

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.databinding.Step9LoginFragmentBinding
import com.zhuinden.simplestacktutorials.steps.step_9.utils.get
import com.zhuinden.simplestacktutorials.steps.step_9.utils.set
import com.zhuinden.simplestacktutorials.utils.onClick
import com.zhuinden.simplestacktutorials.utils.onTextChanged
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

class LoginFragment : KeyedFragment(R.layout.step9_login_fragment) {
    private val viewModel by lazy { lookup<LoginViewModel>() }

    private val compositeDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = Step9LoginFragmentBinding.bind(view)

        binding.textUsername.setText(viewModel.username.get())
        binding.textPassword.setText(viewModel.password.get())

        viewModel.isLoginEnabled.distinctUntilChanged().subscribeBy { enabled ->
            binding.buttonLogin.isEnabled = enabled
        }.addTo(compositeDisposable)

        binding.textUsername.onTextChanged { username -> viewModel.username.set(username) }
        binding.textPassword.onTextChanged { password -> viewModel.password.set(password) }
        binding.buttonLogin.onClick { viewModel.onLoginClicked() }
        binding.buttonRegister.onClick { viewModel.onRegisterClicked() }
    }

    override fun onDestroyView() {
        compositeDisposable.clear()
        super.onDestroyView()
    }
}