package com.gmail.btheo95.aria.model;

import android.util.Log;

import com.gmail.btheo95.aria.network.HttpRequest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 * Created by btheo on 09.11.2016.
 */

public class IpChecker implements Callable<Server> {

    private final static String TAG = IpChecker.class.getSimpleName();
    private String ip;
    private String port;
    private String deviceName;
    private String macAddress;
    private boolean isOpened = false;
    private int timeout;

    public IpChecker(String ip, String port, int timeout) {
        this.ip = ip;
        this.port = port;
        this.timeout = timeout;
    }

    @Override
    public Server call() throws Exception {
        try {
            URL url = new URL("http://" + ip + ":" + port);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(timeout);
            urlConnection.connect();
            Log.v(TAG, "ip is opened:" + ip + ":" + port);
            urlConnection.disconnect();

            deviceName = HttpRequest.getHostName(ip, port);
            macAddress = HttpRequest.getHostMAC(ip, port);
            isOpened = true;
        } catch (MalformedURLException e) {
            Log.v(TAG, "ip is closed(MalformedURLException): " + ip + ":" + port);
            isOpened = false;
        } catch (IOException e) {
            Log.v(TAG, "ip is closed(IOException): " + ip + ":" + port);
            isOpened = false;
        }
        return new Server(ip, port, deviceName, isOpened, macAddress);
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public boolean isOpened() {
        return isOpened;
    }
}
