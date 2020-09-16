package com.zhuinden.simplestackdemoexamplefragments.application

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.SimpleStateChanger
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.core.navigation.FragmentKey
import com.zhuinden.simplestackdemoexamplefragments.data.manager.DatabaseManager
import com.zhuinden.simplestackdemoexamplefragments.features.tasks.TasksKey
import com.zhuinden.simplestackdemoexamplefragments.util.BackstackHolder
import com.zhuinden.simplestackdemoexamplefragments.util.onClick
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentStateChanger
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SimpleStateChanger.NavigationHandler {
    lateinit var fragmentStateChanger: DefaultFragmentStateChanger

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

        this.fragmentStateChanger = DefaultFragmentStateChanger(supportFragmentManager, R.id.root)

        val backstack = Navigator.configure()
            .setScopedServices(DefaultServiceProvider())
            .setStateChanger(SimpleStateChanger(this))
            .setDeferredInitialization(true)
            .install(this, root, History.of(TasksKey()))

        backstackHolder.backstack = backstack

        mainView.onCreate()

        Navigator.executeDeferredInitialization(this)

        buttonAddTask.onClick {
            val top = backstack.top<FragmentKey>()
            val fragment = supportFragmentManager.findFragmentByTag(top.fragmentTag)
            top.fabClickListener(fragment!!).onClick(it)
        }
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

    override fun onNavigationEvent(stateChange: StateChange) {
        fragmentStateChanger.handleStateChange(stateChange)
        mainView.setupViewsForKey(stateChange.topNewKey())
    }
}
