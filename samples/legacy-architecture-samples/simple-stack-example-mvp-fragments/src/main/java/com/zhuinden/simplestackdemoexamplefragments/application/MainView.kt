package com.zhuinden.simplestackdemoexamplefragments.application

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackdemoexamplefragments.R
import com.zhuinden.simplestackdemoexamplefragments.core.navigation.FragmentKey
import com.zhuinden.simplestackdemoexamplefragments.features.statistics.StatisticsKey
import com.zhuinden.simplestackdemoexamplefragments.features.tasks.TasksKey
import com.zhuinden.simplestackdemoexamplefragments.util.findActivity
import com.zhuinden.simplestackdemoexamplefragments.util.showIf
import kotlinx.android.synthetic.main.activity_main.view.*

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

class MainView : DrawerLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var drawerToggle: ActionBarDrawerToggle

    private val navigationItemSelectedListener = NavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.list_navigation_menu_item -> Navigator.getBackstack(context).goTo(TasksKey())
            R.id.statistics_navigation_menu_item -> Navigator.getBackstack(context).goTo(StatisticsKey())
            else -> {
            }
        }
        setCheckedItem(item.getItemId())
        // Close the navigation drawer when an item is selected.
        drawerLayout.closeDrawers()
        true
    }

    private fun setCheckedItem(navigationItemId: Int) {
        val menu = navigationView.menu
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)
            menuItem.isChecked = menuItem.itemId == navigationItemId
        }
    }

    fun setupViewsForKey(key: FragmentKey) {
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

        buttonAddTask.showIf { key.isFabVisible }

        if (key.fabDrawableIcon() != 0) {
            buttonAddTask.setImageResource(key.fabDrawableIcon())
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        drawerLayout = this
    }

    fun onCreate() {
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener)

        context.findActivity<AppCompatActivity>().setSupportActionBar(toolbar)

        val actionBar = context.findActivity<AppCompatActivity>().supportActionBar!!

        drawerToggle = object : ActionBarDrawerToggle(context.findActivity<AppCompatActivity>(), drawerLayout, toolbar, R.string.open, R.string.close) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                context.findActivity<AppCompatActivity>().invalidateOptionsMenu()
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                context.findActivity<AppCompatActivity>().invalidateOptionsMenu()
            }
        }
        @Suppress("DEPRECATION")
        drawerLayout.setDrawerListener(drawerToggle)

        drawerToggle.setToolbarNavigationClickListener { _ -> Navigator.getBackstack(context).goBack() }
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
}
