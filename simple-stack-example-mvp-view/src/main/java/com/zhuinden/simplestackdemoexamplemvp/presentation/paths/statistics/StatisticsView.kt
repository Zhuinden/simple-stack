package com.zhuinden.simplestackdemoexamplemvp.presentation.paths.statistics

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.widget.LinearLayout
import com.zhuinden.simplestackdemoexamplemvp.R
import com.zhuinden.simplestackdemoexamplemvp.application.Injector
import kotlinx.android.synthetic.main.path_statistics.view.*

/**
 * Created by Zhuinden on 2017.01.26..
 */

class StatisticsView : LinearLayout {

    private val myResources: Resources = Injector.get().resources()
    private val statisticsPresenter = Injector.get().statisticsPresenter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    fun setProgressIndicator(active: Boolean) {
        if (active) {
            textStatistics.text = myResources.getString(R.string.loading)
        } else {
            textStatistics.text = ""
        }
    }

    fun showStatistics(numberOfIncompleteTasks: Int, numberOfCompletedTasks: Int) {
        textStatistics.text = when {
            numberOfCompletedTasks == 0 && numberOfIncompleteTasks == 0 -> myResources.getString(R.string.statistics_no_tasks)
            else ->
                "${myResources.getString(R.string.statistics_active_tasks)} $numberOfIncompleteTasks\n" +
                    "${myResources.getString(R.string.statistics_completed_tasks)} $numberOfCompletedTasks"
        }
    }

    fun showLoadingStatisticsError() {
        textStatistics.text = myResources.getString(R.string.statistics_error)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        statisticsPresenter.attachView(this)
    }

    override fun onDetachedFromWindow() {
        statisticsPresenter.detachView(this)
        super.onDetachedFromWindow()
    }
}
