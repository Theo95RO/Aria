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

    public static void copyToClipboard(Context context, String content, String label) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText(label, content);
        clipboard.setPrimaryClip(clip);
    }

}
