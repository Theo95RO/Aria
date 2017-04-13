package com.gmail.btheo95.aria.network;

import android.content.Context;
import android.util.Pair;

import com.gmail.btheo95.aria.model.Server;
import com.gmail.btheo95.aria.utils.Constants;
import com.gmail.btheo95.aria.utils.Database;
import com.gmail.btheo95.aria.utils.Utils;
import com.jaredrummler.android.device.DeviceName;

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

    public static String urlFromIpAndPort(String ip, String port) {
        return "http://" + ip + ":" + port;
    }

    public static String serverUrlForMediaUpload(Context context) {
        Database db = new Database(context);
        Server server = db.getServer();
        String deviceNameAndIMEI = DeviceName.getDeviceName() + " - " + Utils.getDeviceImei(context);
        String serverURL = ("http://" + server.getIp() + ":" + Constants.SERVER_PORT + "/uploadPhoto/" + deviceNameAndIMEI);
        serverURL = serverURL.replaceAll(" ", "_");
        return serverURL;
    }
}
