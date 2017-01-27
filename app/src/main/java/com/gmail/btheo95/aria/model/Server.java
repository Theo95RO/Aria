package com.gmail.btheo95.aria.model;

/**
 * Created by btheo on 25.01.2017.
 */

public class Server {

    private String ip;
    private int port;
    private String deviceName;
    private boolean isOpened = false;
    private String macAdress;

//    public IpCheckerContext(String ip, int port) {
//
//    }

    public Server(String ip, int port, String deviceName, boolean isOpened, String macAdress) {
        this.ip = ip;
        this.port = port;
        this.deviceName = deviceName;
        this.isOpened = isOpened;
        this.macAdress = macAdress;
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

    public String getMacAdress() {
        return macAdress;
    }
}
