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
                .withTargetActivity(MapActivity.class)
                .withSplashTimeOut(2000)
                .withBackgroundColor(Color.parseColor("#ffffff"))
                .withLogo(R.mipmap.ic_launcher)
                .withHeaderText("A Product by CTU & DGIST Students")
                .withAfterLogoText("CTU Explorer")
                .withFooterText("Copyright 2019 by KIV Summer Project Team");

        config.getHeaderTextView().setTextSize(10);
        config.getHeaderTextView().setPadding(0,12,0,0);
        config.getFooterTextView().setTextSize(10);
        config.getFooterTextView().setPadding(0,0,0,12);


        View splashScreen = config.create();
        setContentView(splashScreen);
    }
}
