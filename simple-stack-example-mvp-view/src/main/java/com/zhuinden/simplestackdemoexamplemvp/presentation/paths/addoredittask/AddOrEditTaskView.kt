package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import com.zhuinden.simplestack.Bundleable
import com.zhuinden.simplestackdemoexamplemvp.application.Injector
import com.zhuinden.statebundle.StateBundle
import kotlinx.android.synthetic.main.path_addoredittask.view.*
import org.jetbrains.anko.sdk15.listeners.textChangedListener

/**
 * Created by Owner on 2017. 01. 26..
 */

class AddOrEditTaskView : ScrollView, Bundleable {
    private val addOrEditTaskPresenter = Injector.get().addOrEditTaskPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    
    fun saveTask() {
        addOrEditTaskPresenter.saveTask()
    }

    fun navigateBack() {
        addOrEditTaskPresenter.navigateBack()
    }

    override fun toBundle(): StateBundle {
        return addOrEditTaskPresenter.toBundle()
    }

    override fun fromBundle(bundle: StateBundle?) {
        if (bundle != null) {
            addOrEditTaskPresenter.fromBundle(bundle)
        }
    }

    fun setTitle(title: String) {
        textAddTaskTitle.setText(title)
    }

    fun setDescription(description: String) {
        textAddTaskDescription.setText(description)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addOrEditTaskPresenter.attachView(this)
    }

    override fun onDetachedFromWindow() {
        addOrEditTaskPresenter.detachView(this)
        super.onDetachedFromWindow()
    }
}
