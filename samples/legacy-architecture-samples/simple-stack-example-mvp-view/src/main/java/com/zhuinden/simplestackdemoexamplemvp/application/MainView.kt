package com.zhuinden.simplestackdemoexamplemvp.application

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import com.zhuinden.simplestackdemoexamplemvp.R
import com.zhuinden.simplestackdemoexamplemvp.core.navigation.ViewKey
import com.zhuinden.simplestackdemoexamplemvp.databinding.ActivityMainBinding
import com.zhuinden.simplestackdemoexamplemvp.features.statistics.StatisticsKey
import com.zhuinden.simplestackdemoexamplemvp.features.tasks.TasksKey
import com.zhuinden.simplestackdemoexamplemvp.util.findActivity
import com.zhuinden.simplestackdemoexamplemvp.util.showIf
import com.zhuinden.simplestackextensions.navigatorktx.backstack

/**
 * Created by Owner on 2017. 01. 26..
 */

class MainView : DrawerLayout, MainActivity.OptionsItemSelectedListener, StateChanger {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private lateinit var drawerLayout: DrawerLayout


    private lateinit var drawerToggle: ActionBarDrawerToggle

    private lateinit var binding: ActivityMainBinding // TODO: wtf is this sample?

    private val navigationItemSelectedListener = NavigationView.OnNavigationItemSelectedListener { item ->
        when (item.getItemId()) {
            R.id.list_navigation_menu_item -> backstack.goTo(TasksKey())
            R.id.statistics_navigation_menu_item -> backstack.goTo(StatisticsKey())
            else -> {
            }
        }
        setCheckedItem(item.getItemId())
        // Close the navigation drawer when an item is selected.
        drawerLayout.closeDrawers()
        true
    }

    private fun setCheckedItem(navigationItemId: Int) {
        val menu = binding.navigationView.menu
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)
            menuItem.isChecked = menuItem.itemId == navigationItemId
        }
    }

    fun setupViewsForKey(key: ViewKey, newView: View) {
        val actionBar = context.findActivity<AppCompatActivity>().supportActionBar!!
        if (key.shouldShowUp()) {
            setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START)
            drawerToggle.isDrawerIndicatorEnabled = false
            actionBar.setDisplayHomeAsUpEnabled(true)
        } else {
            setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START)
            actionBar.setDisplayHomeAsUpEnabled(false)
            drawerToggle.isDrawerIndicatorEnabled = true
        }
        drawerToggle.syncState()
        setCheckedItem(key.navigationViewId())
        context.findActivity<AppCompatActivity>().invalidateOptionsMenu()
        binding.buttonAddTask.showIf { key.isFabVisible }
        binding.buttonAddTask.setOnClickListener(key.fabClickListener(newView))
        if (key.fabDrawableIcon() != 0) {
            binding.buttonAddTask.setImageResource(key.fabDrawableIcon())
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding = ActivityMainBinding.bind(this)

        drawerLayout = this
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (binding.viewContainer != null && binding.viewContainer.getChildAt(0) != null) {
            var handled = false

            val child = binding.viewContainer.getChildAt(0)
            if (child is MainActivity.OptionsItemSelectedListener) {
                handled = child.onOptionsItemSelected(item)
            }
            if (handled) {
                return handled
            }
        }
        return false
    }

    fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (binding.viewContainer != null && binding.viewContainer.getChildAt(0) != null) {
            val key = Backstack.getKey<ViewKey>(binding.viewContainer.getChildAt(0).context)
            context.findActivity<AppCompatActivity>().menuInflater.inflate(key.menu(), menu)
            return true
        }
        return false
    }

    fun onCreate() {
        binding.navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener)

        context.findActivity<AppCompatActivity>().setSupportActionBar(binding.toolbar)
        val actionBar = context.findActivity<AppCompatActivity>().supportActionBar!!
        drawerToggle = object : ActionBarDrawerToggle(context.findActivity<AppCompatActivity>(), drawerLayout, binding.toolbar, R.string.open, R.string.close) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                context.findActivity<AppCompatActivity>().invalidateOptionsMenu()
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                context.findActivity<AppCompatActivity>().invalidateOptionsMenu()
            }
        }
        drawerLayout.setDrawerListener(drawerToggle)

        drawerToggle.toolbarNavigationClickListener = OnClickListener { backstack.goBack() }
        actionBar.setDisplayHomeAsUpEnabled(false)
        actionBar.setHomeButtonEnabled(true)
    }

    fun onPostCreate() {
        drawerToggle.syncState()
    }

    fun onConfigChanged(newConfig: Configuration) {
        drawerToggle.onConfigurationChanged(newConfig)
    }

    fun onBackPressed(): Boolean {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
            return true
        }
        return false
    }


    override fun handleStateChange(stateChange: StateChange, callback: StateChanger.Callback) {
        if (binding.viewContainer != null && binding.viewContainer.getChildAt(0) != null) {
            val child = binding.viewContainer.getChildAt(0)
            if (child is StateChanger) {
                child.handleStateChange(stateChange, callback)
                return
            }
        }
        callback.stateChangeComplete()
    }
}
