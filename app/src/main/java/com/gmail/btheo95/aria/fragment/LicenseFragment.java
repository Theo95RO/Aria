package com.gmail.btheo95.aria.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewFragment;

/**
 * Created by btheo on 28.02.2017.
 */

public class LicenseFragment extends WebViewFragment {

    public LicenseFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        LicenseFragment fragment = new LicenseFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        WebView webView = (WebView) super.onCreateView(inflater, container, savedInstanceState);
        webView.loadUrl("file:///android_asset/license.html");
        return webView;
    }


}

