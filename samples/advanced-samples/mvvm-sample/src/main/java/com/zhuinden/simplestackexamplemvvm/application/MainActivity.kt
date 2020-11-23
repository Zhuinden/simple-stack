package com.zhuinden.simplestackexamplemvvm.application


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.SimpleStateChanger
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackexamplemvvm.R
import com.zhuinden.simplestackexamplemvvm.databinding.MainActivityBinding
import com.zhuinden.simplestackexamplemvvm.features.statistics.StatisticsKey
import com.zhuinden.simplestackexamplemvvm.features.tasks.TasksKey
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentStateChanger
import com.zhuinden.simplestackextensions.navigatorktx.backstack
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider

/**
 * Created by Zhuinden on 2017.07.26..
 */
class MainActivity : AppCompatActivity(), SimpleStateChanger.NavigationHandler {
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var fragmentStateChanger: DefaultFragmentStateChanger
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var binding: MainActivityBinding

    private val navigationItemSelectedListener = NavigationView.OnNavigationItemSelectedListener { item: MenuItem ->
        when (item.itemId) {
            R.id.list_navigation_menu_item -> backstack.goTo(TasksKey())
            R.id.statistics_navigation_menu_item -> backstack.goTo(StatisticsKey())
            else -> {
            }
        }
        setCheckedItem(item.itemId)
        // Close the navigation drawer when an item is selected.
        binding.drawerLayout.closeDrawers()
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.drawerLayout.setStatusBarBackground(R.color.colorPrimaryDark)
        binding.navView.setNavigationItemSelectedListener(navigationItemSelectedListener)
        setSupportActionBar(binding.toolbar)
        val actionBar = supportActionBar
        drawerToggle = object : ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.open, R.string.close) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                supportInvalidateOptionsMenu()
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                supportInvalidateOptionsMenu()
            }
        }
        // noinspection deprecation
        binding.drawerLayout.setDrawerListener(drawerToggle)
        drawerToggle.setToolbarNavigationClickListener {
            Navigator.onBackPressed(this)
        }
        actionBar!!.setDisplayHomeAsUpEnabled(false)
        actionBar.setHomeButtonEnabled(true)

        fragmentStateChanger = DefaultFragmentStateChanger(supportFragmentManager, R.id.contentFrame)

        val app = application as CustomApplication

        Navigator.configure()
            .setStateChanger(SimpleStateChanger(this))
            .setGlobalServices(app.globalServices)
            .setScopedServices(DefaultServiceProvider())
            .install(this, binding.contentFrame, History.of(TasksKey()))
    }

    override fun onBackPressed() {
        if (!Navigator.onBackPressed(this)) {
            super.onBackPressed()
        }
    }

    private fun setCheckedItem(navigationItemId: Int) {
        val menu = binding.navView.menu
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)
            menuItem.isChecked = menuItem.itemId == navigationItemId
        }
    }

    fun setupViewsForKey(key: BaseKey) {
        handler.post {
            if (key.shouldShowUp()) {
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START)
                drawerToggle.isDrawerIndicatorEnabled = false
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            } else {
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START)
                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                drawerToggle.isDrawerIndicatorEnabled = true
            }
            drawerToggle.syncState()
            setCheckedItem(key.navigationViewId())
            supportInvalidateOptionsMenu()
            if (key.isFabVisible) {
                binding.fab.show()
            } else {
                binding.fab.hide()
            }
            val fragment = supportFragmentManager.findFragmentByTag(key.fragmentTag)
            key.setupFab(fragment!!, binding.fab)
        }
    }

    override fun onNavigationEvent(stateChange: StateChange) {
        fragmentStateChanger.handleStateChange(stateChange)
        setupViewsForKey(stateChange.topNewKey())
        val title = stateChange.topNewKey<BaseKey>().title(resources)
        setTitle(title ?: getString(R.string.app_name))
    }
}