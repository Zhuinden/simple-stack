package com.zhuinden.simplestackdemoexamplemvp.features.statistics

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackdemoexamplemvp.R
import com.zhuinden.simplestackdemoexamplemvp.core.mvp.MvpPresenter
import com.zhuinden.simplestackdemoexamplemvp.databinding.PathStatisticsBinding

/**
 * Created by Zhuinden on 2017.01.26..
 */

class StatisticsView : LinearLayout {
    companion object {
        const val CONTROLLER_TAG = "StatisticsView.Presenter"
    }

    interface Presenter: MvpPresenter<StatisticsView> {
    }

    private val statisticsPresenter by lazy { Navigator.lookupService<Presenter>(context, CONTROLLER_TAG) }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    private lateinit var binding: PathStatisticsBinding

    fun setProgressIndicator(active: Boolean) {
        binding.textStatistics.text = when {
            active -> context.getString(R.string.loading)
            else -> ""
        }
    }

    fun showStatistics(numberOfIncompleteTasks: Int, numberOfCompletedTasks: Int) {
        binding.textStatistics.text = when {
            numberOfCompletedTasks == 0 && numberOfIncompleteTasks == 0 -> context.getString(R.string.statistics_no_tasks)
            else ->
                "${context.getString(R.string.statistics_active_tasks)} $numberOfIncompleteTasks\n" +
                    "${context.getString(R.string.statistics_completed_tasks)} $numberOfCompletedTasks"
        }
    }

    fun showLoadingStatisticsError() {
        binding.textStatistics.text = context.getString(R.string.statistics_error)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        statisticsPresenter.attachView(this)
    }

    override fun onDetachedFromWindow() {
        statisticsPresenter.detachView(this)
        super.onDetachedFromWindow()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding = PathStatisticsBinding.bind(this)
    }
}
