package com.zhuinden.simplestacktutorials.steps.step_3

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.SimpleStateChanger
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestacktutorials.R
import com.zhuinden.simplestacktutorials.utils.hide
import com.zhuinden.simplestacktutorials.utils.onClick
import com.zhuinden.simplestacktutorials.utils.show
import com.zhuinden.simplestacktutorials.utils.showIf
import kotlinx.android.synthetic.main.activity_step3.*

private val Activity.backstack: Backstack
    get() = Navigator.getBackstack(this)

class Step3Activity : AppCompatActivity(), SimpleStateChanger.NavigationHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step3)

        step3TitleButtonBack.onClick {
            backstack.goBack()
        }

        Navigator.configure()
            .setStateChanger(SimpleStateChanger(this))
            .install(this, step3Root, History.of(Step3FirstScreen()))
    }

    override fun onBackPressed() {
        if (!Navigator.onBackPressed(this)) {
            super.onBackPressed()
        }
    }

    override fun onNavigationEvent(stateChange: StateChange) {
        val newKeys = stateChange.getNewKeys<Step3Screen>()
        step3TitleButtonBack.showIf { newKeys.size > 1 } // show up if can go back

        val topKey = stateChange.topNewKey<Step3Screen>()

        step3TitleText.text = topKey.titleText
        step3CenterText.text = topKey.centerText

        val buttonConfiguration = topKey.buttonConfiguration
        if (buttonConfiguration == null) {
            step3Button.hide()
        } else {
            step3Button.show()
            step3Button.text = buttonConfiguration.buttonText
            step3Button.onClick(buttonConfiguration.buttonAction)
        }
    }
}