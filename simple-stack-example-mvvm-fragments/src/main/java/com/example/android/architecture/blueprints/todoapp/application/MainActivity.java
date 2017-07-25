package com.example.android.architecture.blueprints.todoapp.application;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import com.example.android.architecture.blueprints.todoapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zhuinden on 2017.07.26..
 */

public class MainActivity
        extends AppCompatActivity {
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
    }
}
