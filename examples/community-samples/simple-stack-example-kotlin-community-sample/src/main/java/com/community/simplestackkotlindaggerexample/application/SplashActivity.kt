package com.community.simplestackkotlindaggerexample.application

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.community.simplestackkotlindaggerexample.utils.intentFor

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intentFor<MainActivity>()
        startActivity(intent)
        finish()
    }
}