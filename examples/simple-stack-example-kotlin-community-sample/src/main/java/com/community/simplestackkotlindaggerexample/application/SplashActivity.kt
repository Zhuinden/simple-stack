package com.community.simplestackkotlindaggerexample.application

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.community.simplestackkotlindaggerexample.utils.intentFor

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intentFor<MainActivity>()
        startActivity(intent)
        finish()
    }
}