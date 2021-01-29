package com.zhuinden.simplestackdemomultistack.application

import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.AsyncStateChanger
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import com.zhuinden.simplestackdemomultistack.R
import com.zhuinden.simplestackdemomultistack.core.navigation.Multistack
import com.zhuinden.simplestackdemomultistack.core.navigation.MultistackViewStateChanger
import com.zhuinden.simplestackdemomultistack.features.main.chromecast.ChromeCastKey
import com.zhuinden.simplestackdemomultistack.features.main.cloudsync.CloudSyncKey
import com.zhuinden.simplestackdemomultistack.features.main.list.ListKey
import com.zhuinden.simplestackdemomultistack.features.main.mail.MailKey
import com.zhuinden.simplestackdemomultistack.util.onMenuItemSelected
import it.sephiroth.android.library.bottomnavigation.BottomNavigation

class MainActivity : AppCompatActivity(), MultistackViewStateChanger.AnimationStateListener, AsyncStateChanger.NavigationHandler {
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
                add(CloudSyncKey())
                add(ChromeCastKey())
                add(MailKey())
                add(ListKey())

                if (savedInstanceState != null) {
                    fromBundle(savedInstanceState.getParcelable("multistack"))
                }
            }

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        multistackViewStateChanger = MultistackViewStateChanger(this, multistack, findViewById(R.id.container), this)

        findViewById<BottomNavigation>(R.id.bottomNavigationView).onMenuItemSelected { menuItemId: Int, itemIndex: Int, b: Boolean ->
            multistack.setSelectedStack(StackType.values()[itemIndex].name)
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

    override fun onBackPressed() {
        val backstack = multistack.getSelectedStack()
        if (!backstack.goBack()) {
            super.onBackPressed()
        }
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