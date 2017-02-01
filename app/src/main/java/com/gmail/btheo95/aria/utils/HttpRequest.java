package com.gmail.btheo95.aria.utils;

import android.util.Log;
import android.util.Pair;

import java.io.IOException;

/**
 * Created by btheo on 31.01.2017.
 */

public class HttpRequest {

    private static final String TAG = HttpRequest.class.getSimpleName();

    public static String getHostName(String ip, String port) throws IOException {
        String url = "http://" + ip + ":" + port + "/getHostName";
        Pair<Integer, String> request = Network.httpRequest(url, "GET", true, false, null);
        return request.second;
    }

    public static String getHostMAC(String ip, String port) throws IOException {
        String url = "http://" + ip + ":" + port + "/getHostMAC";
        Pair<Integer, String> request = Network.httpRequest(url, "GET", true, false, null);
        return request.second;
    }
}
