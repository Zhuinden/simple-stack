package com.zhuinden.simplestackextensionsample.features.login

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup
import com.zhuinden.simplestackextensionsample.R
import com.zhuinden.simplestackextensionsample.databinding.LoginFragmentBinding
import com.zhuinden.simplestackextensionsample.utils.get
import com.zhuinden.simplestackextensionsample.utils.onClick
import com.zhuinden.simplestackextensionsample.utils.onTextChanged
import com.zhuinden.simplestackextensionsample.utils.set
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

class LoginFragment : KeyedFragment(R.layout.login_fragment) {
    private val viewModel by lazy { lookup<LoginViewModel>() }

    private val compositeDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = LoginFragmentBinding.bind(view)

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