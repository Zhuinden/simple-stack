package com.zhuinden.simplestackextensionsample.features.registration

import android.os.Bundle
import android.view.View
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.lookup
import com.zhuinden.simplestackextensionsample.R
import com.zhuinden.simplestackextensionsample.databinding.EnterProfileDataFragmentBinding
import com.zhuinden.simplestackextensionsample.utils.get
import com.zhuinden.simplestackextensionsample.utils.onClick
import com.zhuinden.simplestackextensionsample.utils.onTextChanged
import com.zhuinden.simplestackextensionsample.utils.set
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

class EnterProfileDataFragment : KeyedFragment(R.layout.enter_profile_data_fragment) {
    private val viewModel by lazy { lookup<RegistrationViewModel>() }

    private val compositeDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = EnterProfileDataFragmentBinding.bind(view)

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