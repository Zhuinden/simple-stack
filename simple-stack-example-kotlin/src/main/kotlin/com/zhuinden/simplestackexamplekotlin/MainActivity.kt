package com.zhuinden.simplestackexamplekotlin

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import com.zhuinden.simplestack.BackstackDelegate
import com.zhuinden.simplestack.HistoryBuilder
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import java.util.*

class MainActivity : AppCompatActivity(), StateChanger {

    @BindView(R.id.navigation)
    lateinit internal var navigation: BottomNavigationView

    @BindView(R.id.root)
    lateinit internal var root: ViewGroup

    lateinit internal var backstackDelegate: BackstackDelegate
    lateinit internal var fragmentStateChanger: FragmentStateChanger

    override fun onCreate(savedInstanceState: Bundle?) {
        backstackDelegate = BackstackDelegate(null)
        backstackDelegate.onCreate(savedInstanceState,
                lastCustomNonConfigurationInstance,
                HistoryBuilder.single(HomeKey))
        backstackDelegate.registerForLifecycleCallbacks(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        navigation.setOnNavigationItemSelectedListener({ item ->
            when (item.getItemId()) {
                R.id.navigation_home -> {
                    replaceHistory(HomeKey)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_dashboard -> {
                    replaceHistory(DashboardKey())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_notifications -> {
                    replaceHistory(NotificationKey)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        })
        Log.i(TAG, "History [" + Arrays.toString(backstackDelegate.backstack.getHistory<Any>().toTypedArray()) + "]") // from conversion
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

    private fun replaceHistory(rootKey: Any) {
        backstackDelegate.backstack.setHistory(HistoryBuilder.single(rootKey), StateChange.REPLACE)
    }

    fun navigateTo(key: Any) {
        backstackDelegate.backstack.goTo(key)
    }

    override fun getSystemService(name: String): Any? {
        return when {
            name == TAG -> this
            else -> super.getSystemService(name)
        }
    }

    override fun handleStateChange(stateChange: StateChange, completionCallback: StateChanger.Callback) {
        if (stateChange.topNewState<Any>() == stateChange.topPreviousState<Any>()) {
            completionCallback.stateChangeComplete()
            return
        }
        fragmentStateChanger.handleStateChange(stateChange)
        completionCallback.stateChangeComplete()
    }

    companion object {
        private val TAG = "MainActivity"

        @SuppressLint("WrongConstant")
        operator fun get(context: Context): MainActivity {
            return context.getSystemService(TAG) as MainActivity
        }
    }
}
