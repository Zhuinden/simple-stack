package com.zhuinden.simplestacktutorials.steps.step_2

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.SimpleStateChanger
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.utils.hide
import com.zhuinden.simplestacktutorials.utils.onClick
import com.zhuinden.simplestacktutorials.utils.safe
import com.zhuinden.simplestacktutorials.utils.show
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_step2.*

private val Context.backstack: Backstack
    get() = Navigator.getBackstack(this)

class Step2Activity : AppCompatActivity(), SimpleStateChanger.NavigationHandler {
    sealed class Screens : Parcelable {
        @Parcelize
        object First : Screens()

        @Parcelize
        object Second : Screens()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step2)

        Navigator.configure()
            .setStateChanger(SimpleStateChanger(this))
            .install(this, step2Root, History.of(Screens.First)) // auto-install backstack
    }

    override fun onBackPressed() {
        if (!Navigator.onBackPressed(this)) {
            super.onBackPressed()
        }
    }

    override fun onNavigationEvent(stateChange: StateChange) {
        val newKey = stateChange.topNewKey<Screens>()

        when (newKey) {
            Screens.First -> {
                step2Text.text = "First Screen"

                step2Button.show()
                step2Button.onClick {
                    backstack.goTo(Screens.Second)
                }
            }
            Screens.Second -> {
                step2Text.text = "Second Screen"

                step2Button.hide()
            }
        }.safe()
    }
}