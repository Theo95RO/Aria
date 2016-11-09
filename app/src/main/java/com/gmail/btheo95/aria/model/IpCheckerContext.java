package com.gmail.btheo95.aria.model;

/**
 * Created by btheo on 09.11.2016.
 */

public class IpCheckerContext {

    private String ip;
    private int port;
    private String deviceName;
    private boolean isOpened = false;

    public IpCheckerContext(String ip, int port, String deviceName, boolean isOpened) {
        this.ip = ip;
        this.port = port;
        this.deviceName = deviceName;
        this.isOpened = isOpened;
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
