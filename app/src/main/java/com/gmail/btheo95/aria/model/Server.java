package com.gmail.btheo95.aria.model;

/**
 * Created by btheo on 25.01.2017.
 */

public class Server {

    private String ip;
    private String port;
    private String deviceName;
    private boolean isOpened = false;
    private String macAdress;

//    public IpCheckerContext(String ip, int port) {
//
//    }

    public Server(String ip, String port, String deviceName, boolean isOpened, String macAdress) {
        this.ip = ip;
        this.port = port;
        this.deviceName = deviceName;
        this.isOpened = isOpened;
        this.macAdress = macAdress;
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

    public String getMacAdress() {
        return macAdress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Server server = (Server) o;

        if (port != null ? !port.equals(server.port) : server.port != null) return false;
        return macAdress != null ? macAdress.equals(server.macAdress) : server.macAdress == null;

    }
}
