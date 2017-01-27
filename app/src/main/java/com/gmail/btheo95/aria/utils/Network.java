package com.gmail.btheo95.aria.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.gmail.btheo95.aria.Constants;
import com.gmail.btheo95.aria.model.IPv4;
import com.gmail.btheo95.aria.model.IpChecker;
import com.gmail.btheo95.aria.model.IpCheckerContext;
import com.gmail.btheo95.aria.model.Server;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by btheo on 19.01.2017.
 */

public class Network {

    private final static String TAG = Network.class.getSimpleName();

    public static IPv4 getDeviceIp(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

//        The hex literal 0xFF is an equal int(255). Java represents int as 32 bits. It look like this in binary: 00000000 00000000 00000000 11111111. When you do a bit wise AND with this value(255) on any number, it is going to mask(make ZEROs) all but the lowest 8 bits of the number (will be as-is).
        return new IPv4((ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }

    public static String getMacAdressFromIp(String ip) throws SocketException, UnknownHostException {
        return getMacAdressFromIp(InetAddress.getByName(ip));
    }

    public static String getMacAdressFromIp(IPv4 ip) throws SocketException, UnknownHostException {
        return getMacAdressFromIp(ip.toString());
    }

    public static String getMacAdressFromIp(InetAddress ip) throws SocketException {


        NetworkInterface network = NetworkInterface.getByName(ip.getCanonicalHostName());

        return "randomMacAdress";
//        byte[] mac = network.getHardwareAddress();
//
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < mac.length; i++) {
//            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
//        }
//
//        return sb.toString();
    }

    public static List<Server> getServerIps(Context context){
        return getServerIps(context);
    }

    public static List<Server> getLocalServersList(Context context) {
        IPv4 ip = getDeviceIp(context);
        return getServersList(ip);
    }

    public static List<Server> getServersList(String ip) {
        return  getServersList(new IPv4(ip));
    }

    public static List<Server> getServersList(IPv4 currentDiviceIp) {
        //TODO: sa arunc exceptie daca nu e conexiune la net?

        List<Server> serverIpsList = new ArrayList<>();

        final ExecutorService es = Executors.newFixedThreadPool(20);

        IPv4 ip = new IPv4(currentDiviceIp);
        ip.setCell4((short)0);

        final List<Future<Server>> futures = new ArrayList<>();
        for (int i = 1; i <= 256; i++) {
            IpChecker ipChecker = new IpChecker(ip.toString(), Constants.SERVER_PORT, Constants.TIMEOUT_FOR_SERVER_CHECKING);
            Future<Server> future = es.submit(ipChecker);
            futures.add(future);
            ip.increment();
        }

        es.shutdown();
        int openPorts = 0;
        for (final Future<Server> f : futures) {
            try {
                if (f.get().isOpened()) {
                    openPorts++;
                    serverIpsList.add(f.get());
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        Log.i(TAG, "Number of servers found: " + openPorts);
        return serverIpsList;
    }

}
