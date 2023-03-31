package com.zhuinden.simplestackdemomultistack.application

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.AsyncStateChanger
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackdemomultistack.R
import com.zhuinden.simplestackdemomultistack.core.navigation.Multistack
import com.zhuinden.simplestackdemomultistack.core.navigation.MultistackViewStateChanger
import com.zhuinden.simplestackdemomultistack.features.main.chromecast.ChromeCastKey
import com.zhuinden.simplestackdemomultistack.features.main.cloudsync.CloudSyncKey
import com.zhuinden.simplestackdemomultistack.features.main.list.ListKey
import com.zhuinden.simplestackdemomultistack.features.main.mail.MailKey


class MainActivity : AppCompatActivity(), MultistackViewStateChanger.AnimationStateListener, AsyncStateChanger.NavigationHandler {
    @Suppress("DEPRECATION")
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val backstack = multistack.getSelectedStack()

            if (!backstack.goBack()) {
                this.remove()
                onBackPressed() // this is the reliable way to handle back for now when using EVENT_BUBBLING back handling model
                this@MainActivity.onBackPressedDispatcher.addCallback(this)
            }
        }
    }

    lateinit var multistack: Multistack

    private var isAnimating: Boolean = false

    private lateinit var multistackViewStateChanger: MultistackViewStateChanger

    enum class StackType {
        CLOUDSYNC,
        CHROMECAST,
        MAIL,
        LIST
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        this.multistack = (lastCustomNonConfigurationInstance as Multistack?)
            ?: Multistack().apply {
                add(CloudSyncKey)
                add(ChromeCastKey)
                add(MailKey)
                add(ListKey)

                if (savedInstanceState != null) {
                    fromBundle(savedInstanceState.getParcelable("multistack"))
                }
            }

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        onBackPressedDispatcher.addCallback(backPressedCallback) // this is the reliable way to handle back for now

        multistackViewStateChanger = MultistackViewStateChanger(this, multistack, findViewById(R.id.container), this)

        findViewById<View>(R.id.bbn_item1).setOnClickListener {
            multistack.setSelectedStack(StackType.values()[0].name)
        }

        findViewById<View>(R.id.bbn_item2).setOnClickListener {
            multistack.setSelectedStack(StackType.values()[1].name)
        }

        findViewById<View>(R.id.bbn_item3).setOnClickListener {
            multistack.setSelectedStack(StackType.values()[2].name)
        }

        findViewById<View>(R.id.action4).setOnClickListener {
            multistack.setSelectedStack(StackType.values()[3].name)
        }

        multistack.setStateChanger(AsyncStateChanger(this))
    }

    override fun onAnimationStarted() {
        isAnimating = true
    }

    override fun onAnimationEnded() {
        isAnimating = false
    }

    override fun onRetainCustomNonConfigurationInstance(): Any = multistack

    override fun onPostResume() {
        super.onPostResume()
        multistack.unpause()
    }

    override fun onPause() {
        multistack.pause()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("multistack", multistack.toBundle())
        multistack.persistViewToState(findViewById<ViewGroup>(R.id.container).getChildAt(0)) // this is needed for views only
    }

    override fun onDestroy() {
        super.onDestroy()
        multistack.executePendingStateChange()

        if (isFinishing) {
            multistack.finalize()
        }
    }

    override fun getSystemService(name: String): Any? {
        if (::multistack.isInitialized) {
            if (multistack.has(name)) {
                return multistack.get(name)
            }
        }
        return super.getSystemService(name)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return !isAnimating && super.dispatchTouchEvent(ev)
    }

    override fun onNavigationEvent(stateChange: StateChange, completionCallback: StateChanger.Callback) {
        multistackViewStateChanger.handleStateChange(stateChange, completionCallback)
    }
}