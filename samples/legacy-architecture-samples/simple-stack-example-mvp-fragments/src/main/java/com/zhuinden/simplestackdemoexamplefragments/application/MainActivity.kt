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
import com.zhuinden.simplestackdemoexamplefragments.databinding.ActivityMainBinding
import com.zhuinden.simplestackdemoexamplefragments.features.tasks.TasksKey
import com.zhuinden.simplestackdemoexamplefragments.util.get
import com.zhuinden.simplestackdemoexamplefragments.util.viewBinding
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentStateChanger
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider

class MainActivity : AppCompatActivity(), SimpleStateChanger.NavigationHandler {
    private val binding by viewBinding(ActivityMainBinding::inflate)

    lateinit var fragmentStateChanger: DefaultFragmentStateChanger

    override fun onCreate(savedInstanceState: Bundle?) {
        val app = application as CustomApplication

        val globalServices = app.globalServices

        val databaseManager = globalServices.get<DatabaseManager>()
        databaseManager.init(this)

        super.onCreate(savedInstanceState)

        // this must be after `super.onCreate` otherwise multiple of them would exist after process death
        supportFragmentManager.findFragmentByTag("MAIN_SCOPE_LISTENER").also { mainScopeListener ->
            if (mainScopeListener == null) {
                supportFragmentManager.beginTransaction().add(MainScopeListener(), "MAIN_SCOPE_LISTENER").commit()
                supportFragmentManager.executePendingTransactions() // this guarantees that the retained fragment exists by the time the Repository needs it.
            }
        }

        setContentView(binding.root)

        this.fragmentStateChanger = DefaultFragmentStateChanger(supportFragmentManager, R.id.fragmentContainer)

        val backstack = Navigator.configure()
            .setGlobalServices(globalServices)
            .setScopedServices(DefaultServiceProvider())
            .setStateChanger(SimpleStateChanger(this))
            .setDeferredInitialization(true)
            .install(this, binding.fragmentContainer, History.of(TasksKey()))

        binding.mainView.onCreate()

        Navigator.executeDeferredInitialization(this)

        binding.buttonAddTask.setOnClickListener {
            val top = backstack.top<FragmentKey>()
            val fragment = supportFragmentManager.findFragmentByTag(top.fragmentTag)
            top.fabClickListener(fragment!!).onClick(it)
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        binding.mainView.onPostCreate()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.mainView.onConfigChanged(newConfig)
    }

    override fun onBackPressed() {
        if (binding.mainView.onBackPressed()) {
            return
        }
        if (!Navigator.onBackPressed(this)) {
            super.onBackPressed()
        }
    }

    override fun onNavigationEvent(stateChange: StateChange) {
        fragmentStateChanger.handleStateChange(stateChange)
        binding.mainView.setupViewsForKey(stateChange.topNewKey())
    }
}
