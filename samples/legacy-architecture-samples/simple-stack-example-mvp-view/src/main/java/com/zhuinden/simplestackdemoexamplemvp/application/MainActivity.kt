package com.zhuinden.simplestackdemoexamplemvp.application

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.AsyncStateChanger
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackdemoexamplemvp.core.navigation.ViewStateChanger
import com.zhuinden.simplestackdemoexamplemvp.data.manager.DatabaseManager
import com.zhuinden.simplestackdemoexamplemvp.databinding.ActivityMainBinding
import com.zhuinden.simplestackdemoexamplemvp.features.tasks.TasksKey
import com.zhuinden.simplestackdemoexamplemvp.util.viewBinding
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import com.zhuinden.simplestackextensions.servicesktx.get

class MainActivity : AppCompatActivity(), AsyncStateChanger.NavigationHandler {
    private val binding by viewBinding(ActivityMainBinding::inflate)

    interface OptionsItemSelectedListener {
        fun onOptionsItemSelected(menuItem: MenuItem): Boolean
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        binding.mainView.onOptionsItemSelected(item)

    override fun onCreateOptionsMenu(menu: Menu): Boolean =
        binding.mainView.onCreateOptionsMenu(menu)

    private lateinit var viewStateChanger: ViewStateChanger

    override fun onCreate(savedInstanceState: Bundle?) {
        val app = application as CustomApplication
        val globalServices = app.globalServices

        val databaseManager = globalServices.get<DatabaseManager>()

        databaseManager.init(this)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewStateChanger = ViewStateChanger(this, binding.viewContainer)

        val backstack = Navigator.configure()
            .setGlobalServices(globalServices)
            .setScopedServices(DefaultServiceProvider())
            .setShouldPersistContainerChild(true)
            .setDeferredInitialization(true)
            .setStateChanger(AsyncStateChanger(this))
            .install(this, binding.viewContainer, History.single(TasksKey()))

        val mainScopeListener: MainScopeListener? = supportFragmentManager.findFragmentByTag(
            "MAIN_SCOPE_LISTENER") as MainScopeListener?
        if (mainScopeListener == null) {
            supportFragmentManager.beginTransaction().add(MainScopeListener(), "MAIN_SCOPE_LISTENER").commit()
        }

        binding.mainView.onCreate()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        Navigator.executeDeferredInitialization(this)
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

    override fun onNavigationEvent(stateChange: StateChange, completionCallback: StateChanger.Callback) {
        viewStateChanger.handleStateChange(stateChange) {
            binding.mainView.handleStateChange(stateChange) {
                binding.mainView.setupViewsForKey(stateChange.topNewKey(), binding.viewContainer.getChildAt(0))
                completionCallback.stateChangeComplete()
            }
        }
    }
}