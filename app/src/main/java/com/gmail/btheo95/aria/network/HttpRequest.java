package com.gmail.btheo95.aria.network;

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

    public static boolean isAriaServer(String ip, String port) throws IOException {
        String url = urlFromIpAndPort(ip, port) + "/isAriaServer";
        Pair<Integer, String> request = Network.httpRequest(url, "GET", true, false, null);
        return request.first == 200 && request.second.equals("true");
    }

    public static boolean isSameMacOnServer(String ip, String port, String mac) throws IOException {
        String url = urlFromIpAndPort(ip, port) + "/isYourMAC";
        Pair<Integer, String> request = Network.httpRequest(url, "POST", true, true, mac);
        return request.first == 200 && request.second.equals("true");
    }

    private static String urlFromIpAndPort(String ip, String port) {
        return "http://" + ip + ":" + port;
    }
}
