package com.zhuinden.simplestackdemoexamplemvp.features.addoredittask

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackdemoexamplemvp.core.mvp.MvpPresenter
import com.zhuinden.simplestackdemoexamplemvp.databinding.PathAddoredittaskBinding
import com.zhuinden.simplestackdemoexamplemvp.util.onTextChanged

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

    private lateinit var binding: PathAddoredittaskBinding

    fun setTitle(title: String) {
        binding.textAddTaskTitle.setText(title)
    }

    fun setDescription(description: String) {
        binding.textAddTaskDescription.setText(description)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        binding = PathAddoredittaskBinding.bind(this)

        binding.textAddTaskTitle.onTextChanged { title ->
            addOrEditTaskPresenter.onTitleChanged(title)
        }

        binding.textAddTaskDescription.onTextChanged { description ->
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
