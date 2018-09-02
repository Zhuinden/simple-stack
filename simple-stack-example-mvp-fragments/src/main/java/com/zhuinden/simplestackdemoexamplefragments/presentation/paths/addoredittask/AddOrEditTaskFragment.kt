package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.addoredittask

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.zhuinden.simplestackdemoexamplefragments.util.BaseFragment
import com.zhuinden.simplestackdemoexamplefragments.util.MvpPresenter
import com.zhuinden.simplestackdemoexamplefragments.util.backstackDelegate
import kotlinx.android.synthetic.main.path_addoredittask.*
import org.jetbrains.anko.sdk15.listeners.textChangedListener

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

// UNSCOPED!
class AddOrEditTaskFragment : BaseFragment<AddOrEditTaskFragment, AddOrEditTaskFragment.Presenter>() {
    companion object {
        const val CONTROLLER_TAG = "AddOrEditTaskPresenter"
    }

    interface Presenter: MvpPresenter<AddOrEditTaskFragment> {
        fun onTitleChanged(title: String)

        fun onDescriptionChanged(description: String)

        fun onSaveButtonClicked()
    }
    
    override val presenter: Presenter by lazy {
        backstackDelegate.lookupService<AddOrEditTaskFragment.Presenter>(CONTROLLER_TAG)
    }

    override fun getThis(): AddOrEditTaskFragment = this

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textAddTaskTitle.textChangedListener {
            afterTextChanged { editable ->
                presenter.onTitleChanged(editable.toString())
            }
        }

        textAddTaskDescription.textChangedListener {
            afterTextChanged { editable ->
                presenter.onDescriptionChanged(editable.toString())
            }
        }
    }

    fun setTitle(title: String) {
        textAddTaskTitle.setText(title)
    }

    fun setDescription(description: String) {
        textAddTaskDescription.setText(description)
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
