package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.addoredittask

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.zhuinden.simplestackdemoexamplefragments.application.Injector
import com.zhuinden.simplestackdemoexamplefragments.util.BaseFragment
import kotlinx.android.synthetic.main.path_addoredittask.*
import org.jetbrains.anko.sdk15.listeners.textChangedListener

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

// UNSCOPED!
class AddOrEditTaskFragment : BaseFragment<AddOrEditTaskPresenter.ViewContract, AddOrEditTaskPresenter>(), AddOrEditTaskPresenter.ViewContract {
    private val addOrEditTaskPresenter = Injector.get().addOrEditTaskPresenter()

    override fun getPresenter(): AddOrEditTaskPresenter = addOrEditTaskPresenter

    override fun getThis(): AddOrEditTaskFragment = this

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textAddTaskTitle.textChangedListener {
            afterTextChanged { editable ->
                addOrEditTaskPresenter.updateTitle(editable.toString())
            }
        }

        textAddTaskDescription.textChangedListener {
            afterTextChanged { editable ->
                addOrEditTaskPresenter.updateDescription(editable.toString())
            }
        }
    }

    fun saveTask() = addOrEditTaskPresenter.saveTask()

    fun navigateBack() {
        addOrEditTaskPresenter.navigateBack()
    }

    override fun setTitle(title: String) {
        textAddTaskTitle.setText(title)
    }

    override fun setDescription(description: String) {
        textAddTaskDescription.setText(description)
    }

    override fun hideKeyboard() {
        (requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).also { imm ->
            val view = view
            if(view != null) {
                imm.hideSoftInputFromWindow(view.windowToken, 0);
            }
        }
    }
}
