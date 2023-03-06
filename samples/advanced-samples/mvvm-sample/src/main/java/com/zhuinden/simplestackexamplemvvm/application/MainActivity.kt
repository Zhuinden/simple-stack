package com.zhuinden.simplestackexamplemvvm.application


import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.zhuinden.liveevent.observe
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.SimpleStateChanger
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackexamplemvvm.R
import com.zhuinden.simplestackexamplemvvm.databinding.MainActivityBinding
import com.zhuinden.simplestackexamplemvvm.features.statistics.StatisticsKey
import com.zhuinden.simplestackexamplemvvm.features.tasks.TasksKey
import com.zhuinden.simplestackexamplemvvm.util.showSnackbar
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentStateChanger
import com.zhuinden.simplestackextensions.navigatorktx.backstack
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import com.zhuinden.simplestackextensions.servicesktx.get

class MainActivity : AppCompatActivity(), SimpleStateChanger.NavigationHandler {
    private lateinit var fragmentStateChanger: DefaultFragmentStateChanger
    private lateinit var snackbarTextEmitter: SnackbarTextEmitter
    private lateinit var binding: MainActivityBinding

    @Suppress("DEPRECATION")
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!Navigator.onBackPressed(this@MainActivity)) {
                this.remove()
                onBackPressed()  // this is the reliable way to handle back for now
                this@MainActivity.onBackPressedDispatcher.addCallback(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(backPressedCallback) // this is the reliable way to handle back for now

        val app = application as CustomApplication
        val globalServices = app.globalServices

        snackbarTextEmitter = globalServices.get()

        binding.drawerLayout.setStatusBarBackground(R.color.colorPrimaryDark)

        binding.navView.setNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.list_navigation_menu_item -> backstack.goTo(TasksKey)
                R.id.statistics_navigation_menu_item -> backstack.goTo(StatisticsKey)
                else -> {
                }
            }

            setCheckedItem(item.itemId)

            binding.drawerLayout.closeDrawers()
            true
        }

        fragmentStateChanger = DefaultFragmentStateChanger(supportFragmentManager, R.id.contentFrame)

        snackbarTextEmitter.snackbarText.observe(this) { textRes ->
            showSnackbar(binding.root, getString(textRes))
        }

        Navigator.configure()
            .setStateChanger(SimpleStateChanger(this))
            .setGlobalServices(globalServices)
            .setScopedServices(DefaultServiceProvider())
            .install(this, binding.contentFrame, History.of(TasksKey))
    }

    fun toggleLeftDrawer() {
        val drawerLayout = binding.drawerLayout

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setCheckedItem(navigationItemId: Int) {
        val menu = binding.navView.menu
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)
            menuItem.isChecked = menuItem.itemId == navigationItemId
        }
    }

    override fun onNavigationEvent(stateChange: StateChange) {
        binding.drawerLayout.setDrawerLockMode(when {
            stateChange.getNewKeys<BaseKey>().size > 1 -> DrawerLayout.LOCK_MODE_LOCKED_CLOSED
            else -> DrawerLayout.LOCK_MODE_UNLOCKED
        }, GravityCompat.START)

        val topNewKey = stateChange.topNewKey<BaseKey>()

        if (topNewKey is TasksKey) {
            setCheckedItem(R.id.list_navigation_menu_item)
        }

        if (topNewKey is StatisticsKey) {
            setCheckedItem(R.id.statistics_navigation_menu_item)
        }

        fragmentStateChanger.handleStateChange(stateChange)
    }
}