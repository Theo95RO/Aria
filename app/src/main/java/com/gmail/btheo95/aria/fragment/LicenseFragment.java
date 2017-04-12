package com.gmail.btheo95.aria.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.webkit.WebView;

import com.gmail.btheo95.aria.R;

/**
 * Created by btheo on 28.02.2017.
 */

public class LicenseFragment extends DialogFragment {

    public LicenseFragment() {
        // Required empty public constructor
    }

    public static LicenseFragment newInstance() {
        return new LicenseFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        WebView view = (WebView) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_license, null);
        view.loadUrl("file:///android_asset/license.html");
        return new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle(getString(R.string.fragment_license_title))
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }
}

