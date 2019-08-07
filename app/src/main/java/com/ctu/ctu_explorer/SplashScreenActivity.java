package com.ctu.ctu_explorer;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EasySplashScreen config = new EasySplashScreen(SplashScreenActivity.this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(5000)
                .withBackgroundColor(Color.parseColor("#ffffff"))
                .withLogo(R.mipmap.ic_launcher)
                .withAfterLogoText("CTU Explorer")
                .withFooterText("Copyright 2019 by KIV Summer Project Team");

        View splashScreen = config.create();
        setContentView(splashScreen);
    }
}
