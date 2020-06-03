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
import kotlinx.android.synthetic.main.enter_profile_data_fragment.*

class EnterProfileDataFragment : KeyedFragment(R.layout.enter_profile_data_fragment) {
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