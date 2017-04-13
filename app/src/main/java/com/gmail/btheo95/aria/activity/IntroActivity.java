package com.gmail.btheo95.aria.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.gmail.btheo95.aria.R;
import com.gmail.btheo95.aria.utils.Permissions;
import com.gmail.btheo95.aria.utils.Utils;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;

public class IntroActivity extends MaterialIntroActivity {

    private static String TAG = IntroActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //enableLastSlideAlphaExitTransition(true);

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.intro_1_background)
                .buttonsColor(R.color.intro_1_buttons)
                .title(getString(R.string.intro_1_title))
                .image(R.drawable.ic_iphone)
                .description(getString(R.string.intro_1_description))
                .build()
        );

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.intro_2_background)
                .buttonsColor(R.color.intro_2_buttons)
                        .title(getString(R.string.server_download_url))
                .description(getString(R.string.intro_2_description))
                .image(R.drawable.ic_direction)
                .build(),
                new MessageButtonBehaviour(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        shareUrl();
//                        showMessage(getString(R.string.toast_after_button_download_click));
                    }
                }, getString(R.string.button_download_link_text))
        );

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.intro_3_background)
                .buttonsColor(R.color.intro_3_buttons)
                .title(getString(R.string.intro_3_title))
                .image(R.drawable.ic_traffic_light)
                .description(getString(R.string.intro_3_description))
                .neededPermissions(Permissions.allPermissions)
                .build());

    }

    private void shareUrl() {
        Utils.startShareIntent(this, getString(R.string.intro_2_intent_share_url_title), getString(R.string.server_download_http_url));
    }


    @Override
    public void onFinish() {

        if(Permissions.areGranted(this)){

            Log.d(TAG, "Permission are granted -> returning RESULT_OK");

            if (getParent() == null) {
                Log.d(TAG, "Parent activity is null");
                setResult(Activity.RESULT_OK);
            } else {
                getParent().setResult(Activity.RESULT_OK);
            }
        }

        super.onFinish();
    }

    private void copyDownloadUrlToClipboard() {

        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", getString(R.string.URL_download));
        clipboard.setPrimaryClip(clip);

    }

}
