package com.zhuinden.simplestackdemoexamplemvp.features.statistics

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.widget.LinearLayout
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackdemoexamplemvp.R
import com.zhuinden.simplestackdemoexamplemvp.application.Injector
import com.zhuinden.simplestackdemoexamplemvp.core.mvp.MvpPresenter
import kotlinx.android.synthetic.main.path_statistics.view.*

/**
 * Created by Zhuinden on 2017.01.26..
 */

class StatisticsView : LinearLayout {
    companion object {
        const val CONTROLLER_TAG = "StatisticsView.Presenter"
    }

    interface Presenter: MvpPresenter<StatisticsView> {
    }


    private val myResources: Resources = Injector.get().resources()

    private val statisticsPresenter by lazy { Navigator.lookupService<Presenter>(context, CONTROLLER_TAG) }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    fun setProgressIndicator(active: Boolean) {
        textStatistics.text = when {
            active -> myResources.getString(R.string.loading)
            else -> ""
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
