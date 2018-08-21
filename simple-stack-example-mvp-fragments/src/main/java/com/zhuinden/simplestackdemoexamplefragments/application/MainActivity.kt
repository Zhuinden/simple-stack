package com.zhuinden.simplestackdemoexamplefragments.application

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zhuinden.simplestack.BackstackDelegate
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.data.manager.DatabaseManager
import com.zhuinden.simplestackdemoexamplefragments.presentation.paths.tasks.TasksKey
import com.zhuinden.simplestackdemoexamplefragments.util.BackstackHolder
import com.zhuinden.simplestackdemoexamplefragments.util.FragmentStateChanger
import com.zhuinden.simplestackdemoexamplefragments.util.scopedservices.ServiceProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), StateChanger {
    lateinit var backstackDelegate: BackstackDelegate
    lateinit var fragmentStateChanger: FragmentStateChanger

    private val databaseManager: DatabaseManager = Injector.get().databaseManager()
    private val backstackHolder: BackstackHolder = Injector.get().backstackHolder()

    override fun onCreate(savedInstanceState: Bundle?) {
        databaseManager.init(this)

        backstackDelegate = BackstackDelegate()
        backstackDelegate.setScopedServices(this, ServiceProvider())
        backstackDelegate.onCreate(savedInstanceState, //
            lastCustomNonConfigurationInstance, //
            History.single(TasksKey()))

        backstackDelegate.registerForLifecycleCallbacks(this)

        backstackHolder.backstack = backstackDelegate.backstack

        super.onCreate(savedInstanceState)

        supportFragmentManager.findFragmentByTag("MAIN_SCOPE_LISTENER").also { mainScopeListener ->
            if (mainScopeListener == null) {
                supportFragmentManager.beginTransaction().add(MainScopeListener(), "MAIN_SCOPE_LISTENER").commit()
            }
        }

        setContentView(R.layout.activity_main)

        this.fragmentStateChanger = FragmentStateChanger(supportFragmentManager, R.id.root)
        mainView.onCreate()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        backstackDelegate.setStateChanger(this)
        mainView.onPostCreate()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mainView.onConfigChanged(newConfig)
    }

    override fun onRetainCustomNonConfigurationInstance(): Any =
        backstackDelegate.onRetainCustomNonConfigurationInstance()

    override fun onBackPressed() {
        if (mainView.onBackPressed()) {
            return
        }
        if (!backstackDelegate.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun getSystemService(name: String): Any? = when {
        TAG == name -> this
        else -> super.getSystemService(name)
    }

    override fun handleStateChange(stateChange: StateChange, completionCallback: StateChanger.Callback) {
        if (stateChange.topNewState<Any>() == stateChange.topPreviousState()) {
            // no-op
            completionCallback.stateChangeComplete()
            return
        }

        fragmentStateChanger.handleStateChange(stateChange)

        mainView.setupViewsForKey(stateChange.topNewState())
        completionCallback.stateChangeComplete()
    }

    companion object {
        const val TAG = "MainActivity"

        @SuppressLint("WrongConstant")
        operator fun get(context: Context): MainActivity {
            return context.getSystemService(TAG) as MainActivity
        }
    }
}
