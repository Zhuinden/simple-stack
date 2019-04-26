package com.zhuinden.simplestackexamplekotlin

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zhuinden.simplestack.BackstackDelegate
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.KeyChange
import com.zhuinden.simplestack.KeyChanger
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Owner on 2017.11.13.
 */
class MainActivity : AppCompatActivity(), KeyChanger {
    private lateinit var backstackDelegate: BackstackDelegate
    private lateinit var fragmentKeyChanger: FragmentKeyChanger

    override fun onCreate(savedInstanceState: Bundle?) {
        backstackDelegate = BackstackDelegate()
        backstackDelegate.onCreate(savedInstanceState,
                lastCustomNonConfigurationInstance,
                History.single(HomeKey()))
        backstackDelegate.registerForLifecycleCallbacks(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener { item ->
            val destination = when(item.itemId) {
                R.id.navigation_home -> HomeKey()
                R.id.navigation_dashboard -> DashboardKey()
                R.id.navigation_notifications -> NotificationKey()
                else -> null
            }
            destination?.let { key ->
                replaceHistory(key)
                true
            } ?: false
        }
        fragmentKeyChanger = FragmentKeyChanger(supportFragmentManager, R.id.root)
        backstackDelegate.setKeyChanger(this)
    }

    override fun onRetainCustomNonConfigurationInstance() =
            backstackDelegate.onRetainCustomNonConfigurationInstance()

    override fun onBackPressed() {
        if (!backstackDelegate.onBackPressed()) {
            super.onBackPressed()
        }
    }

    private fun replaceHistory(rootKey: BaseKey) {
        backstackDelegate.backstack.setHistory(History.single(rootKey), KeyChange.REPLACE)
    }

    fun navigateTo(key: BaseKey) {
        backstackDelegate.backstack.goTo(key)
    }

    override fun handleKeyChange(keyChange: KeyChange, completionCallback: KeyChanger.Callback) {
        if (keyChange.isTopNewKeyEqualToPrevious) {
            completionCallback.keyChangeComplete()
            return
        }
        fragmentKeyChanger.handleKeyChange(keyChange)
        completionCallback.keyChangeComplete()
    }

    // share activity through context
    override fun getSystemService(name: String): Any? = when {
        name == TAG -> this
        else -> super.getSystemService(name)
    }

    companion object {
        private val TAG = "MainActivity"

        @SuppressLint("WrongConstant")
        operator fun get(context: Context): MainActivity {
            return context.getSystemService(TAG) as MainActivity
        }
    }
}
