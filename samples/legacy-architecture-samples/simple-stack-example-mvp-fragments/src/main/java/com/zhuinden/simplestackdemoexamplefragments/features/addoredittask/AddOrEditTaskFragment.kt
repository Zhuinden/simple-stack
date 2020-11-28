package com.zhuinden.simplestackdemoexamplefragments.features.addoredittask

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.zhuinden.simplestackdemoexamplefragments.core.mvp.MvpPresenter
import com.zhuinden.simplestackdemoexamplefragments.core.navigation.BaseFragment
import com.zhuinden.simplestackdemoexamplefragments.databinding.PathAddoredittaskBinding
import com.zhuinden.simplestackdemoexamplefragments.util.lookup
import com.zhuinden.simplestackdemoexamplefragments.util.onTextChanged
import com.zhuinden.simplestackdemoexamplefragments.util.viewBinding

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

// UNSCOPED!
class AddOrEditTaskFragment : BaseFragment() {
    companion object {
        const val CONTROLLER_TAG = "AddOrEditTaskPresenter"
    }

    interface Presenter: MvpPresenter<AddOrEditTaskFragment> {
        fun onTitleChanged(title: String)

        fun onDescriptionChanged(description: String)

        fun onSaveButtonClicked()
    }

    val presenter: Presenter by lazy {
        lookup<Presenter>(CONTROLLER_TAG)
    }

    private val binding by viewBinding(PathAddoredittaskBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.attachView(this)

        binding.textAddTaskTitle.onTextChanged { title ->
            presenter.onTitleChanged(title)
        }

        binding.textAddTaskDescription.onTextChanged { description ->
            presenter.onDescriptionChanged(description)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        presenter.detachView(this)
    }

    fun setTitle(title: String) {
        binding.textAddTaskTitle.setText(title)
    }

    fun setDescription(description: String) {
        binding.textAddTaskDescription.setText(description)
    }

    fun hideKeyboard() {
        (requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).also { imm ->
            val view = view
            if(view != null) {
                imm.hideSoftInputFromWindow(view.windowToken, 0);
            }
        }
    }

    fun onSaveButtonClicked() {
        presenter.onSaveButtonClicked()
    }
}
