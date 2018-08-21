package com.zhuinden.simplestackdemoexamplemvp.application

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestack.navigator.changehandlers.FadeViewChangeHandler
import com.zhuinden.simplestackdemoexamplemvp.R
import com.zhuinden.simplestackdemoexamplemvp.presentation.paths.tasks.TasksKey
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), StateChanger {
    private val databaseManager = Injector.get().databaseManager()
    private val backstackHolder = Injector.get().backstackHolder()

    interface OptionsItemSelectedListener {
        fun onOptionsItemSelected(menuItem: MenuItem): Boolean
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        mainView.onOptionsItemSelected(item)


    override fun onCreateOptionsMenu(menu: Menu): Boolean =
        mainView.onCreateOptionsMenu(menu)

    override fun onCreate(savedInstanceState: Bundle?) {
        databaseManager.init(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val backstack = Navigator.configure()
            .setDeferredInitialization(true)
            .setStateChanger(this)
            .install(this, root, History.single(TasksKey.create()))
        backstackHolder.backstack = backstack

        val mainScopeListener: MainScopeListener? = supportFragmentManager.findFragmentByTag(
            "MAIN_SCOPE_LISTENER") as MainScopeListener?
        if (mainScopeListener == null) {
            supportFragmentManager.beginTransaction().add(MainScopeListener(), "MAIN_SCOPE_LISTENER").commit()
        }

        mainView.onCreate()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        Navigator.executeDeferredInitialization(this)
        mainView.onPostCreate()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mainView.onConfigChanged(newConfig)
    }

    override fun onBackPressed() {
        if (mainView.onBackPressed()) {
            return
        }
        if (!Navigator.onBackPressed(this)) {
            super.onBackPressed()
        }
    }

    override fun getSystemService(name: String): Any? = when {
        TAG == name -> this
        else -> super.getSystemService(name)
    }

    override fun handleStateChange(stateChange: StateChange, completionCallback: StateChanger.Callback) {
        if (stateChange.topNewState<Any>() == stateChange.topPreviousState<Any>()) {
            completionCallback.stateChangeComplete()
            return
        }
        val newKey = stateChange.topNewState<Key>()
        val previousKey = stateChange.topPreviousState<Key?>()
        val newView = LayoutInflater.from(stateChange.createContext(this, newKey)).inflate(newKey.layout(), root, false)

        val previousView = root.getChildAt(0)
        Navigator.persistViewToState(previousView)
        Navigator.restoreViewFromState(newView)

        if (previousKey == null || previousView == null) {
            root.addView(newView)
            mainView.setupViewsForKey(newKey, newView)
            completionCallback.stateChangeComplete()
            return
        }

        val viewChangeHandler = when (stateChange.direction) {
            StateChange.FORWARD -> newKey.viewChangeHandler()
            StateChange.BACKWARD -> previousKey.viewChangeHandler()
            else -> FadeViewChangeHandler()
        }

        viewChangeHandler.performViewChange(root, previousView, newView, stateChange.direction) {
            mainView.handleStateChange(stateChange) {
                mainView.setupViewsForKey(newKey, newView)
                completionCallback.stateChangeComplete()
            }
        }
    }

    companion object {
        const val TAG = "MainActivity"

        @SuppressLint("WrongConstant")
        @JvmStatic
        operator fun get(context: Context): MainActivity =
            context.getSystemService(TAG) as MainActivity
    }
}
