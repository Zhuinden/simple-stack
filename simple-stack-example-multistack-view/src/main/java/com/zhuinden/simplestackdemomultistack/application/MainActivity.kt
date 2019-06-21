package com.zhuinden.simplestackdemomultistack.application

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import com.zhuinden.simplestackdemomultistack.R
import com.zhuinden.simplestackdemomultistack.core.navigation.Multistack
import com.zhuinden.simplestackdemomultistack.core.navigation.MultistackViewStateChanger
import com.zhuinden.simplestackdemomultistack.features.main.chromecast.ChromeCastKey
import com.zhuinden.simplestackdemomultistack.features.main.cloudsync.CloudSyncKey
import com.zhuinden.simplestackdemomultistack.features.main.list.ListKey
import com.zhuinden.simplestackdemomultistack.features.main.mail.MailKey
import com.zhuinden.simplestackdemomultistack.util.onMenuItemSelected
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MultistackViewStateChanger.AnimationStateListener {
    lateinit var multistack: Multistack

    private var isAnimating: Boolean = false

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

        bottomNavigationView.onMenuItemSelected { menuItemId: Int, itemIndex: Int, b: Boolean ->
            multistack.setSelectedStack(StackType.values()[itemIndex].name)
        }
        multistack.setStateChanger(MultistackViewStateChanger(this, multistack, root, this))
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
        multistack.persistViewToState(root.getChildAt(0))
        outState.putParcelable("multistack", multistack.toBundle())
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
}