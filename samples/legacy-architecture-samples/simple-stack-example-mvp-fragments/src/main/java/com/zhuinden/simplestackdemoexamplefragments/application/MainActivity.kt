package com.zhuinden.simplestackdemoexamplefragments.application

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.core.navigation.FragmentStateChanger
import com.zhuinden.simplestackdemoexamplefragments.data.manager.DatabaseManager
import com.zhuinden.simplestackdemoexamplefragments.features.tasks.TasksKey
import com.zhuinden.simplestackdemoexamplefragments.util.BackstackHolder
import com.zhuinden.simplestackdemoexamplefragments.util.scopedservices.ServiceProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), StateChanger {
    lateinit var fragmentStateChanger: FragmentStateChanger

    private val databaseManager: DatabaseManager = Injector.get().databaseManager()
    private val backstackHolder: BackstackHolder = Injector.get().backstackHolder()

    override fun onCreate(savedInstanceState: Bundle?) {
        databaseManager.init(this)

        super.onCreate(savedInstanceState)

        // this must be after `super.onCreate` otherwise multiple of them would exist after process death
        supportFragmentManager.findFragmentByTag("MAIN_SCOPE_LISTENER").also { mainScopeListener ->
            if (mainScopeListener == null) {
                supportFragmentManager.beginTransaction().add(MainScopeListener(), "MAIN_SCOPE_LISTENER").commit()
                supportFragmentManager.executePendingTransactions() // this guarantees that the retained fragment exists by the time the Repository needs it.
            }
        }

        setContentView(R.layout.activity_main)

        this.fragmentStateChanger = FragmentStateChanger(supportFragmentManager, R.id.root)

        val backstack = Navigator.configure()
            .setScopedServices(ServiceProvider())
            .setStateChanger(this)
            .setDeferredInitialization(true)
            .install(this, root, History.of(TasksKey()))

        backstackHolder.backstack = backstack

        mainView.onCreate()

        Navigator.executeDeferredInitialization(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
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
            // no-op
            completionCallback.stateChangeComplete()
            return
        }

        fragmentStateChanger.handleStateChange(stateChange)
        mainView.setupViewsForKey(stateChange.topNewKey())
        completionCallback.stateChangeComplete()
    }

    companion object {
        @SuppressLint("WrongConstant")
        operator fun get(context: Context): MainActivity =
            context.getSystemService(TAG) as MainActivity

        const val TAG = "MainActivity"
    }
}
