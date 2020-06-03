package com.zhuinden.simplestackextensionsample.features.login

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup
import com.zhuinden.simplestackextensionsample.R
import com.zhuinden.simplestackextensionsample.utils.get
import com.zhuinden.simplestackextensionsample.utils.set
import com.zhuinden.simplestackextensionsample.utils.onClick
import com.zhuinden.simplestackextensionsample.utils.onTextChanged
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.login_fragment.*

class LoginFragment : KeyedFragment(R.layout.login_fragment) {
    private val viewModel by lazy { lookup<LoginViewModel>() }

    private val compositeDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textUsername.setText(viewModel.username.get())
        textPassword.setText(viewModel.password.get())

        viewModel.isLoginEnabled.distinctUntilChanged().subscribeBy { enabled ->
            buttonLogin.isEnabled = enabled
        }.addTo(compositeDisposable)

        textUsername.onTextChanged { username -> viewModel.username.set(username) }
        textPassword.onTextChanged { password -> viewModel.password.set(password) }
        buttonLogin.onClick { viewModel.onLoginClicked() }
        buttonRegister.onClick { viewModel.onRegisterClicked() }
    }

    override fun onDestroyView() {
        compositeDisposable.clear()
        super.onDestroyView()
    }
}