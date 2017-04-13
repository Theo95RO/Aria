package com.gmail.btheo95.aria.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import com.gmail.btheo95.aria.R;
import com.permissioneverywhere.PermissionEverywhere;
import com.permissioneverywhere.PermissionResponse;

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

    public static boolean arePermissionGrantedInBackgroud(Context context) {
        PermissionResponse response = null;
        try {
            response = PermissionEverywhere.getPermission(context,
                    Permissions.allPermissions,
                    0,
                    context.getString(R.string.notification_permissions_title),
                    context.getString(R.string.notification_permissions_content),
                    R.drawable.ic_error_outline_black_24dp)
                    .call();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        if (response == null) {
            return false;
        }
        //waits...
        return response.isGranted();
    }


}
