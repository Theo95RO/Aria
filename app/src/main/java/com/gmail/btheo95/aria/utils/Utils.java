package com.gmail.btheo95.aria.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by btheo on 09.03.2017.
 */

public class Utils {

    public static long getDeviceImei(Context context) {

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        return Long.parseLong(imei);
    }

}
