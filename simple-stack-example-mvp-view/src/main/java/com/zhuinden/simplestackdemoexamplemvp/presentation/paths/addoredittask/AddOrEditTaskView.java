package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.addoredittask;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ScrollView;

import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestackdemoexamplemvp.R;
import com.zhuinden.simplestackdemoexamplemvp.application.Injector;
import com.zhuinden.statebundle.StateBundle;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

/**
 * Created by Owner on 2017. 01. 26..
 */

public class AddOrEditTaskView
        extends ScrollView
        implements Bundleable {
    public AddOrEditTaskView(Context context) {
        super(context);
        init(context);
    }

    public AddOrEditTaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AddOrEditTaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public AddOrEditTaskView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        if(!isInEditMode()) {
            addOrEditTaskPresenter = Injector.get()
                    .addOrEditTaskPresenter();
        }
    }

    AddOrEditTaskPresenter addOrEditTaskPresenter;

    @OnTextChanged(R.id.add_task_title)
    public void titleChanged(Editable editable) {
        addOrEditTaskPresenter.updateTitle(editable.toString());
    }

    @OnTextChanged(R.id.add_task_description)
    public void descriptionChanged(Editable editable) {
        addOrEditTaskPresenter.updateDescription(editable.toString());
    }

    @BindView(R.id.add_task_title)
    EditText addTaskTitle;

    @BindView(R.id.add_task_description)
    EditText addTaskDescription;

    public void saveTask() {
        addOrEditTaskPresenter.saveTask();
    }

    public void navigateBack() {
        addOrEditTaskPresenter.navigateBack();
    }

    @Override
    public StateBundle toBundle() {
        return addOrEditTaskPresenter.toBundle();
    }

    @Override
    public void fromBundle(@Nullable StateBundle bundle) {
        if(bundle != null) {
            addOrEditTaskPresenter.fromBundle(bundle);
        }
    }

    public void setTitle(String title) {
        addTaskTitle.setText(title);
    }

    public void setDescription(String description) {
        addTaskDescription.setText(description);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        addOrEditTaskPresenter.attachView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        addOrEditTaskPresenter.detachView(this);
        super.onDetachedFromWindow();
    }
}
