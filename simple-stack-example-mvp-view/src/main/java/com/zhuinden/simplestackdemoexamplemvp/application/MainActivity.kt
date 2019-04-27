package com.zhuinden.simplestackdemoexamplemvp.application

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackdemoexamplemvp.R
import com.zhuinden.simplestackdemoexamplemvp.core.navigation.ViewStateChanger
import com.zhuinden.simplestackdemoexamplemvp.features.tasks.TasksKey
import com.zhuinden.simplestackdemoexamplemvp.util.scoping.ServiceProvider
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

    private lateinit var viewStateChanger: ViewStateChanger

    override fun onCreate(savedInstanceState: Bundle?) {
        databaseManager.init(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewStateChanger = ViewStateChanger(this, root)
        val backstack = Navigator.configure()
            .setScopedServices(ServiceProvider())
            .setShouldPersistContainerChild(true)
            .setDeferredInitialization(true)
            .setStateChanger(this)
            .install(this, root, History.single(TasksKey()))
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
        if (stateChange.isTopNewKeyEqualToPrevious) {
            completionCallback.stateChangeComplete()
            return
        }

        viewStateChanger.handleStateChange(stateChange) {
            mainView.handleStateChange(stateChange) {
                mainView.setupViewsForKey(stateChange.topNewKey(), root.getChildAt(0))
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
