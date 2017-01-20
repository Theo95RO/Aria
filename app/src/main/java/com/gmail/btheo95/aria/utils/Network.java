package com.gmail.btheo95.aria.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.gmail.btheo95.aria.Constants;
import com.gmail.btheo95.aria.model.IPv4;
import com.gmail.btheo95.aria.model.IpChecker;
import com.gmail.btheo95.aria.model.IpCheckerContext;

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

    public IPv4 getDeviceIp(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        return new IPv4((ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }

    public List<IpCheckerContext> getServerIps(Context context){
        return getServerIps(context);
    }

    private List<IpCheckerContext> getServerIps(IPv4 currentDiviceIp) {
        //TODO: sa primesc ca parametru ip normal sub forma de String si apoi ii fac conversie la IPv4. Daca nu are forma buna arunc exceptie
        //TODO: sa arunc exceptie daca nu e conexiune la net?

        List<IpCheckerContext> serverIpsList = new ArrayList<>();

        final ExecutorService es = Executors.newFixedThreadPool(20);

        IPv4 ip = new IPv4(currentDiviceIp);
        ip.setCell4((short)0);

        final List<Future<IpCheckerContext>> futures = new ArrayList<>();
        for (int i = 1; i <= 256; i++) {
            futures.add(ipIsOpen(es, ip.toString(), Constants.serverPort, Constants.timeoutForServerChecking));
            ip.increment();
        }

        es.shutdown();
        int openPorts = 0;
        for (final Future<IpCheckerContext> f : futures) {
            try {
                if (f.get().isOpened()) {
                    openPorts++;
                    serverIpsList.add(f.get());
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "Number of servers found: " + openPorts);
        return serverIpsList;
    }

    private Future<IpCheckerContext> ipIsOpen(final ExecutorService es, final String ip, final int port, final int timeout) {

        //Log.v(TAG, "Checking if ip is open: " + ip + ":" + port);

        return es.submit(new IpChecker(ip, port, timeout));
    }
}
