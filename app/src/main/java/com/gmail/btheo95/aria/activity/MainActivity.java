package com.gmail.btheo95.aria.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.gmail.btheo95.aria.R;
import com.gmail.btheo95.aria.fragment.ServersFragment;
import com.gmail.btheo95.aria.fragment.SettingsFragment;
import com.gmail.btheo95.aria.fragment.StatusFragment;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static String TAG = MainActivity.class.getSimpleName();

    private FloatingActionButton fab;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private NavigationView navigationView;

    public static final String PREF_KEY_FIRST_START = "com.gmail.btheo95.aria.PREF_KEY_FIRST_START";
    public static final int REQUEST_CODE_INTRO = 1;
    public static final int REQUEST_CODE_PICK_FILE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(MainActivity.this, IntroActivity.class);
                //startActivityForResult(intent, REQUEST_CODE_INTRO);
                //setTargetPromptForFAB();
                startPickFileActivity();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //sa nu mai faca animatia toggle-ul
                super.onDrawerSlide(drawerView, 0);
            }
        };

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        boolean firstStart = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PREF_KEY_FIRST_START, true);

        if (firstStart) {
            Intent intent = new Intent(this, IntroActivity.class);
            startActivityForResult(intent, REQUEST_CODE_INTRO);
        }
        else{
            //Set the fragment initially
            setMainFragmentWithoutAnimation(StatusFragment.newInstance());
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //@SuppressWarnings("StatementWithEmptyBody")
    //@NonNull at MenuItem because Android Studio suggested this way, not sure why
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            setMainFragment(SettingsFragment.newInstance());
            fab.hide();

        } else if (id == R.id.nav_status) {
            setMainFragment(StatusFragment.newInstance());
            fab.show();
        } else if (id == R.id.nav_servers) {
            setMainFragment(ServersFragment.newInstance());
            fab.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult() -> resultCode = " + resultCode + " requestCode = " + requestCode);

        //REQUEST_CODE_PICK_FILE
        if (requestCode == REQUEST_CODE_INTRO) {
            if (resultCode == RESULT_OK) {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putBoolean(PREF_KEY_FIRST_START, false)
                        .apply();

                //Set the fragment initially
                setMainFragmentWithoutAnimation(StatusFragment.newInstance());
                setTargetPromptForFAB();
            }
            else {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putBoolean(PREF_KEY_FIRST_START, true)
                        .apply();
                //User cancelled the intro so we'll finish this activity too.
                finish();
            }
        }

        else if (requestCode == REQUEST_CODE_INTRO) {
            if (resultCode == RESULT_OK) {
                // TODO: upload the file.
                Toast.makeText(this, "A file has been selected", Toast.LENGTH_LONG);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setTargetPromptForFAB() {
        Log.d(TAG, "setTargetPromptForFAB()");
        new MaterialTapTargetPrompt.Builder(MainActivity.this)

                .setBackgroundColourFromRes(R.color.primary)
                .setTarget(findViewById(R.id.fab))
                .setPrimaryText(getString(R.string.fab_prompt_primary_text))
                .setSecondaryText(getString(R.string.fab_prompt_secondary_text))
                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener()
                {
                    @Override
                    public void onHidePrompt(MotionEvent event, boolean tappedTarget)
                    {
                        //Do something such as storing a value so that this prompt is never shown again
                    }

                    @Override
                    public void onHidePromptComplete()
                    {

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

    private void setMainFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();


        fragmentTransaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left);

        fragmentTransaction.replace(R.id.content_main, fragment);
        fragmentTransaction.commit();
    }


    private void setMainFragmentWithoutAnimation(Fragment fragment) {
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.content_main, fragment);
        fragmentTransaction.commit();
    }

    private void startPickFileActivity(){
        Log.d(TAG, "startPickFileActivity()");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_FILE);
    }
}
