package com.gmail.btheo95.aria.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.gmail.btheo95.aria.R;
import com.gmail.btheo95.aria.service.MediaJobService;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final static String TAG = SettingsFragment.class.getSimpleName();

    private Context mCotext;

    public SettingsFragment() {
        // Required empty public constructor
    }


    public static  android.app.Fragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCotext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_settings);
        PreferenceManager.getDefaultSharedPreferences(mCotext).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        MediaJobService.restartNewMediaJob(mCotext.getApplicationContext());
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        getPreferenceScreen().getSharedPreferences()
//                .unregisterOnSharedPreferenceChangeListener(this);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        getPreferenceScreen().getSharedPreferences()
//                .unregisterOnSharedPreferenceChangeListener(this);
//    }

    //    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        //return inflater.inflate(R.xml.fragment_settings, container, false);
//
//        return addPreferencesFromResource(R.xml.fragment_settings);;
//    }

}
