package com.gmail.btheo95.aria.activity;

import android.Manifest;
import android.os.Bundle;

import com.gmail.btheo95.aria.R;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

public class IntroductionActivity extends com.heinrichreimersoftware.materialintro.app.IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setButtonCtaVisible(false);

        addSlide(new SimpleSlide.Builder()
                .title("titlu")
                .description("descriereee")
                .background(R.color.accent)
                .backgroundDark(R.color.primary)
                .permission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .build());


        addSlide(new SimpleSlide.Builder()
                .title("titlu")
                .description("descriereee")
                .background(R.color.primary)
                .backgroundDark(R.color.accent)
                .build());



    }
}
