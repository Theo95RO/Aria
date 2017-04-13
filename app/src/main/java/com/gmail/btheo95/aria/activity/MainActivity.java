package com.gmail.btheo95.aria.activity;

import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.gmail.btheo95.aria.R;
import com.gmail.btheo95.aria.fragment.AboutFragment;
import com.gmail.btheo95.aria.fragment.LicenseFragment;
import com.gmail.btheo95.aria.fragment.ServersFragment;
import com.gmail.btheo95.aria.fragment.SettingsFragment;
import com.gmail.btheo95.aria.fragment.StatusFragment;
import com.gmail.btheo95.aria.service.CustomFileUploadIntentService;
import com.gmail.btheo95.aria.service.MediaJobService;
import com.gmail.btheo95.aria.utils.FileUtils;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AboutFragment.OnFragmentInteractionListener {

    private static String TAG = MainActivity.class.getSimpleName();

    private FloatingActionButton mFab;
    private ActionBarDrawerToggle mHamburgerToggle;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawer;

    private int mCurrentNavigationItemId;
    private int mLastNavigationItemId = -1;

    public static final String PREF_KEY_FIRST_START = "com.gmail.btheo95.aria.PREF_KEY_FIRST_START";
    public static final String KEY_DEFAULT_NAV_ITEM = "com.gmail.btheo95.aria.KEY_DEFAULT_NAV_ITEM";
    public static final int REQUEST_CODE_INTRO = 1;
    public static final int REQUEST_CODE_PICK_FILE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseViews();
        initialiseRecentView();

        boolean firstStart = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PREF_KEY_FIRST_START, true);

        if (firstStart) {
            Intent intent = new Intent(this, IntroActivity.class);
            startActivityForResult(intent, REQUEST_CODE_INTRO);
        }
        //Set the fragment initially
        else {
            setDefaultFragment();
        }
    }

    private void setDefaultFragment() {
        int defaultNavigationItemId = getIntent().getIntExtra(KEY_DEFAULT_NAV_ITEM, R.id.nav_status);
        setFragmentByNavigationItemId(defaultNavigationItemId, null, null);
        mCurrentNavigationItemId = defaultNavigationItemId;
        mNavigationView.setCheckedItem(defaultNavigationItemId);
    }

    private void initialiseRecentView() {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), android.R.drawable.sym_def_app_icon);
        ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, ContextCompat.getColor(this, R.color.primary_recent));
        setTaskDescription(taskDesc);
    }

    private void initialiseViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPickFileActivity();
            }
        });

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mHamburgerToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, 0);
            }
        };

        mHamburgerToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mCurrentNavigationItemId == R.id.nav_about) {
            setFragmentByNavigationItemId(mLastNavigationItemId, R.animator.fade_in, R.animator.fade_out);
        } else {
            super.onBackPressed();
        }
    }

    //@SuppressWarnings("StatementWithEmptyBody")
    //@NonNull at MenuItem because Android Studio suggested this way, not sure why
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        setFragmentByNavigationItemId(id, R.animator.slide_in_right, R.animator.slide_out_left);

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setFragmentByNavigationItemId(int id, Integer idOfInAnimation, Integer idOfOutAnimation) {

        if (mCurrentNavigationItemId != id) {
            mLastNavigationItemId = mCurrentNavigationItemId;
            mCurrentNavigationItemId = id;
            Fragment fragment = null;
            if (id == R.id.nav_settings) {
                fragment = SettingsFragment.newInstance();
                mFab.hide();

            } else if (id == R.id.nav_status) {
                fragment = StatusFragment.newInstance();
                mFab.show();
            } else if (id == R.id.nav_servers) {
                fragment = ServersFragment.newInstance();
                mFab.show();
            } else if (id == R.id.nav_about) {
                fragment = AboutFragment.newInstance();
                mFab.hide();
            }
            setMainFragment(fragment, idOfInAnimation, idOfOutAnimation);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult() -> resultCode = " + resultCode + " requestCode = " + requestCode);

        if (requestCode == REQUEST_CODE_INTRO) {
            if (resultCode == RESULT_OK) {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putBoolean(PREF_KEY_FIRST_START, false)
                        .apply();

                setDefaultFragment();
                MediaJobService.restartNewMediaJob(getApplicationContext());
                setTargetPromptForFAB();
            } else {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putBoolean(PREF_KEY_FIRST_START, true)
                        .apply();
                //User cancelled the intro so we'll finish this activity too.
                finish();
            }
        } else if (requestCode == REQUEST_CODE_PICK_FILE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                String path = FileUtils.getPath(this, uri);
                CustomFileUploadIntentService.startActionUpload(getBaseContext(), path);
//                Toast.makeText(this, "A file has been selected", Toast.LENGTH_LONG).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("hasRotated", true);
        savedInstanceState.putInt("currentNavigationItemId", mCurrentNavigationItemId);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.d(TAG, "onRestoreInstanceState()");

        int id = savedInstanceState.getInt("currentNavigationItemId");
        setFragmentByNavigationItemId(id, null, null);
    }

    @Override
    public void onLicenseClicked() {
        LicenseFragment dialog = LicenseFragment.newInstance();
        dialog.show(getFragmentManager(), "LicensesDialog"); //TODO: set tag
    }

    private void setTargetPromptForFAB() {
        Log.d(TAG, "setTargetPromptForFAB()");
        new MaterialTapTargetPrompt.Builder(MainActivity.this)

                .setBackgroundColourFromRes(R.color.primary)
                .setTarget(findViewById(R.id.fab))
                .setPrimaryText(getString(R.string.fab_prompt_primary_text))
                .setSecondaryText(getString(R.string.fab_prompt_secondary_text))
                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                    @Override
                    public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                        //Do something such as storing a value so that this prompt is never shown again
                    }

                    @Override
                    public void onHidePromptComplete() {

                    }
                })
                .show();

//        new TapTargetSequence(MainActivity.this)
//                .targets(
//                        TapTarget.forView(findViewById(R.id.fab), getString(R.string.fab_prompt_primary_text),getString(R.string.fab_prompt_secondary_text))
//                                // All options below are optional
//                                .drawShadow(true)
//                                .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
//                                .tintTarget(false)                   // Whether to tint the target view's color
//                                .transparentTarget(false))         // Specify whether the target is transparent (displays the content underneath)
//
//
//                .listener(new TapTargetSequence.Listener() {
//                    // This listener will tell us when interesting(tm) events happen in regards
//                    // to the sequence
//                    @Override
//                    public void onSequenceFinish() {
//                        // Yay
//                    }
//
//                    @Override
//                    public void onSequenceCanceled(TapTarget lastTarget) {
//                        // Boo
//                    }
//                }).start();

//        TapTargetView.showFor(this,                 // `this` is an Activity
//                TapTarget.forView(findViewById(R.id.fab), getString(R.string.fab_prompt_primary_text),getString(R.string.fab_prompt_secondary_text))
//                        // All options below are optional
//
//                        .drawShadow(true)
//                        .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
//                        .tintTarget(false)                   // Whether to tint the target view's color
//                        .transparentTarget(false),           // Specify whether the target is transparent (displays the content underneath)
//
//                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
//                    @Override
//                    public void onTargetClick(TapTargetView view) {
//                        super.onTargetClick(view);      // This call is optional
//                    }
//                });
    }

    private void setMainFragment(Fragment fragment, Integer idOfInAnimation, Integer idOfOutAnimation) {

        if (null == fragment) {
            return;
        }

        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();

        if (null != idOfInAnimation && null != idOfOutAnimation) {
            fragmentTransaction.setCustomAnimations(idOfInAnimation, idOfOutAnimation);
        }

        fragmentTransaction.replace(R.id.content_main, fragment);
        fragmentTransaction.commit();
    }

    private void setMainFragmentWithFadeAnimation(Fragment fragment) {
        setMainFragment(fragment, R.animator.fade_in, R.animator.fade_out);
    }

    private void setMainFragmentWithoutAnimation(Fragment fragment) {
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.content_main, fragment);
        fragmentTransaction.commit();
    }

    private void startPickFileActivity() {
        try {
            Log.d(TAG, "startPickFileActivity()");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, REQUEST_CODE_PICK_FILE);
        } catch (android.content.ActivityNotFoundException ex) {
            // TODO: Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
