package com.zhuinden.simplestacktutorials.steps.step_9.features.login

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.steps.step_9.utils.get
import com.zhuinden.simplestacktutorials.steps.step_9.utils.set
import com.zhuinden.simplestacktutorials.utils.onClick
import com.zhuinden.simplestacktutorials.utils.onTextChanged
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.step9_login_fragment.*

class LoginFragment : KeyedFragment(R.layout.step9_login_fragment) {
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