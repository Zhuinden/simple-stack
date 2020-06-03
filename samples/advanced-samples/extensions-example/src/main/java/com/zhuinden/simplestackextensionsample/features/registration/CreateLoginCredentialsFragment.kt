package com.zhuinden.simplestackextensionsample.features.registration

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup
import com.zhuinden.simplestackextensionsample.R
import com.zhuinden.simplestackextensionsample.utils.get
import com.zhuinden.simplestackextensionsample.utils.onClick
import com.zhuinden.simplestackextensionsample.utils.onTextChanged
import com.zhuinden.simplestackextensionsample.utils.set
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.create_login_credentials_fragment.*


class CreateLoginCredentialsFragment : KeyedFragment(R.layout.create_login_credentials_fragment) {
    private val viewModel by lazy { lookup<RegistrationViewModel>() }

    private val compositeDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textUsername.setText(viewModel.username.get())
        textPassword.setText(viewModel.password.get())

        viewModel.isRegisterAndLoginEnabled.distinctUntilChanged().subscribeBy { enabled ->
            buttonRegisterAndLogin.isEnabled = enabled
        }.addTo(compositeDisposable)

        textUsername.onTextChanged { username -> viewModel.username.set(username) }
        textPassword.onTextChanged { password -> viewModel.password.set(password) }
        buttonRegisterAndLogin.onClick { viewModel.onRegisterAndLoginClicked() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }
}