package com.zhuinden.simplestackexamplekotlin

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zhuinden.simplestack.BackstackDelegate
import com.zhuinden.simplestack.HistoryBuilder
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Owner on 2017.11.13.
 */
class MainActivity : AppCompatActivity(), StateChanger {
    private lateinit var backstackDelegate: BackstackDelegate
    private lateinit var fragmentStateChanger: FragmentStateChanger

    override fun onCreate(savedInstanceState: Bundle?) {
        backstackDelegate = BackstackDelegate(null)
        backstackDelegate.onCreate(savedInstanceState,
                lastCustomNonConfigurationInstance,
                HistoryBuilder.single(HomeKey()))
        backstackDelegate.registerForLifecycleCallbacks(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener { item ->
            val destination = when(item.itemId) {
                R.id.navigation_home -> HomeKey()
                R.id.navigation_dashboard -> DashboardKey()
                R.id.navigation_notifications -> NotificationKey()
                else -> null
            }
            destination?.let { key ->
                replaceHistory(key)
                true
            } ?: false
        }
        fragmentStateChanger = FragmentStateChanger(supportFragmentManager, R.id.root)
        backstackDelegate.setStateChanger(this)
    }

    override fun onRetainCustomNonConfigurationInstance() =
            backstackDelegate.onRetainCustomNonConfigurationInstance()

    override fun onBackPressed() {
        if (!backstackDelegate.onBackPressed()) {
            super.onBackPressed()
        }
    }

    private fun replaceHistory(rootKey: BaseKey) {
        backstackDelegate.backstack.setHistory(HistoryBuilder.single(rootKey), StateChange.REPLACE)
    }

    fun navigateTo(key: BaseKey) {
        backstackDelegate.backstack.goTo(key)
    }

    override fun handleStateChange(stateChange: StateChange, completionCallback: StateChanger.Callback) {
        if (stateChange.topNewState<Any>() == stateChange.topPreviousState<Any>()) {
            completionCallback.stateChangeComplete()
            return
        }
        fragmentStateChanger.handleStateChange(stateChange)
        completionCallback.stateChangeComplete()
    }

    // share activity through context
    override fun getSystemService(name: String): Any? = when {
        name == TAG -> this
        else -> super.getSystemService(name)
    }

    companion object {
        private val TAG = "MainActivity"

        @SuppressLint("WrongConstant")
        operator fun get(context: Context): MainActivity {
            return context.getSystemService(TAG) as MainActivity
        }
    }
}
