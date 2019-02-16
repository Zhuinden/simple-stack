package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackdemoexamplemvp.util.MvpPresenter
import com.zhuinden.simplestackdemoexamplemvp.util.onTextChanged
import kotlinx.android.synthetic.main.path_addoredittask.view.*

/**
 * Created by Owner on 2017. 01. 26..
 */

class AddOrEditTaskView : ScrollView {
    companion object {
        const val CONTROLLER_TAG = "AddOrEditTaskView.Presenter"
    }

    interface Presenter : MvpPresenter<AddOrEditTaskView> {
        fun onTitleChanged(title: String)

        fun onDescriptionChanged(description: String)

        fun onSaveButtonClicked()
    }

    private val addOrEditTaskPresenter by lazy { Navigator.lookupService<Presenter>(context, CONTROLLER_TAG) }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    fun setTitle(title: String) {
        textAddTaskTitle.setText(title)
    }

    fun setDescription(description: String) {
        textAddTaskDescription.setText(description)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        textAddTaskTitle.onTextChanged { title ->
            addOrEditTaskPresenter.onTitleChanged(title)
        }

        textAddTaskDescription.onTextChanged { description ->
            addOrEditTaskPresenter.onDescriptionChanged(description)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addOrEditTaskPresenter.attachView(this)
    }

    override fun onDetachedFromWindow() {
        addOrEditTaskPresenter.detachView(this)
        super.onDetachedFromWindow()
    }

    fun fabClicked() {
        addOrEditTaskPresenter.onSaveButtonClicked()
    }
}
