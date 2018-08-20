package com.zhuinden.simplestackdemoexamplefragments.presentation.paths.addoredittask

import android.text.Editable
import android.view.View
import butterknife.ButterKnife
import butterknife.OnTextChanged
import butterknife.Unbinder
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.application.Injector
import com.zhuinden.simplestackdemoexamplefragments.util.BaseFragment
import kotlinx.android.synthetic.main.path_addoredittask.*

/**
 * Created by Owner on 2017. 01. 26..
 */

// UNSCOPED!
class AddOrEditTaskFragment : BaseFragment<AddOrEditTaskFragment, AddOrEditTaskPresenter>() {
    private lateinit var addOrEditTaskPresenter: AddOrEditTaskPresenter

    
    @OnTextChanged(R.id.textAddTaskTitle) // TODO: use Anko's textChangedListener
    fun titleChanged(editable: Editable) {
        addOrEditTaskPresenter.updateTitle(editable.toString())
    }

    @OnTextChanged(R.id.textAddTaskDescription) // TODO: use Anko's textChangedListener
    fun descriptionChanged(editable: Editable) {
        addOrEditTaskPresenter.updateDescription(editable.toString())
    }

    override fun getPresenter(): AddOrEditTaskPresenter = addOrEditTaskPresenter

    override fun getThis(): AddOrEditTaskFragment = this

    override fun bindViews(view: View): Unbinder = ButterKnife.bind(this, view)

    override fun injectSelf() {
        addOrEditTaskPresenter = Injector.get().addOrEditTaskPresenter()
    }

    fun saveTask() = addOrEditTaskPresenter.saveTask()

    fun navigateBack() {
        addOrEditTaskPresenter.navigateBack()
    }

    fun setTitle(title: String) {
        textAddTaskTitle.setText(title)
    }

    fun setDescription(description: String) {
        textAddTaskDescription.setText(description)
    }
}
