package com.gmail.btheo95.aria.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.gmail.btheo95.aria.service.ServerScannerService;

public class WifiWatcher extends BroadcastReceiver {

    private final static String TAG = WifiWatcher.class.getSimpleName();
    private static boolean firstConnect = true;

    public WifiWatcher() {
        Log.d(TAG, "In Constructor");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "In onReceive()");

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if (info != null &&
                info.isConnected() &&
                info.getType() == ConnectivityManager.TYPE_WIFI) {

            if (firstConnect) {
                Log.d(TAG, "Connected on wifi");
                Intent serviceIntent = new Intent(context, ServerScannerService.class);
                context.startService(serviceIntent);
                firstConnect = false;
            }


        } else {

            Log.d(TAG, "Disconnected from wifi");

            firstConnect = true;

            Intent serviceIntent = new Intent(context, ServerScannerService.class);
            context.stopService(serviceIntent);
        }

    }
}
