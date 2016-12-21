package com.gmail.btheo95.aria.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by btheo on 21.12.2016.
 */

public class Permissions {

    public static String[] allPermissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE};

    public static boolean areGranted(Activity activity) {

        for (String permission : allPermissions) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }


}
