package com.gmail.btheo95.aria.model;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 * Created by btheo on 09.11.2016.
 */

public class IpChecker implements Callable<IpCheckerContext>{

    private final static String TAG = IpChecker.class.getSimpleName();
    private String ip;
    private int port;
    private String deviceName; //TODO:
    private boolean isOpened = false;
    private int timeout;

    public IpChecker(String ip, int port, int timeout){
        this.ip = ip;
        this.port = port;
        this.timeout = timeout;
    }
    @Override
    public IpCheckerContext call() throws Exception {
        try {
            URL url = new URL("http://" + ip + ":" + port);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(timeout);
            urlConnection.connect();
            Log.v(TAG, "ip is opened:" + ip + ":" + port);
            urlConnection.disconnect();

            // TODO: sa incerc conexiunea doar prin linia de jos si saii prind eraorea ei
            InetAddress address = InetAddress.getByName(ip);
            deviceName = address.getHostName();

            isOpened = true;
        } catch (MalformedURLException e) {
            Log.v(TAG, "ip is closed(MalformedURLException): " + ip + ":" + port);
            isOpened = false;
        } catch (IOException e) {
            Log.v(TAG, "ip is closed(IOException): " + ip + ":" + port);
            isOpened = false;
        }
        return new IpCheckerContext(ip, port, deviceName, isOpened);
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public boolean isOpened() {
        return isOpened;
    }
}
