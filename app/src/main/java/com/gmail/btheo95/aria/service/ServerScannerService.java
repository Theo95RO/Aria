package com.gmail.btheo95.aria.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.gmail.btheo95.aria.Constants;
import com.gmail.btheo95.aria.Database;
import com.gmail.btheo95.aria.HttpFileUpload;
import com.gmail.btheo95.aria.Utils;
import com.gmail.btheo95.aria.model.IPv4;
import com.gmail.btheo95.aria.model.IpChecker;
import com.gmail.btheo95.aria.model.IpCheckerContext;
import com.gmail.btheo95.aria.model.Server;
import com.jaredrummler.android.device.DeviceName;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ServerScannerService extends IntentService {

    private final static String TAG = ServerScannerService.class.getSimpleName();

    private Database db;

    public ServerScannerService() {
        super("ServerScannerService");
        db = new Database(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "in service");
        if (intent != null) {

            IPv4 ip = getIpAddress();
            Log.d(TAG, "Device local ip: " + ip);

            List<Server> serverIpsList = getServerIps(ip);
            try {
                List<File> photosList = Utils.getPhotosAfterDate(db.getLastDate(), this);
                File[] photosArray = new File[photosList.size()];
                photosList.toArray(photosArray);
                db.addFiles(photosArray);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (serverIpsList.size() > 0) {
                File[] filesToUpload = db.getAllPhotos();
                String deviceNameAndImei = DeviceName.getDeviceName() + " - " + Utils.getDeviceImei(this);
                String serverUri = ("http://" + serverIpsList.get(0).getIp()+ ":" + Constants.SERVER_PORT + "/uploadPhoto/" + deviceNameAndImei);
                serverUri = serverUri.replaceAll(" ", "_");
                HttpFileUpload httpFileUpload = null;
                try {
                    httpFileUpload = new HttpFileUpload(serverUri);
                    int count  = 0;
                    for (File file : filesToUpload) {
                        try {
                            httpFileUpload.sendNow(file);
                            db.removeFile(file);
                            Log.v(TAG, "1 file uploaded");
                        } catch (IOException e) {
                            Log.v(TAG, "1 file upload failed");
                        }
                        if (count++ == 20) break; //for debugging
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

            db.close();
        }
    }

    private List<Server> getServerIps(IPv4 currentDiviceIp) {
        List<Server> serverIpsList = new ArrayList<>();

        final ExecutorService es = Executors.newFixedThreadPool(20);
        final int timeout = 200;

        IPv4 ip = new IPv4(currentDiviceIp);
        ip.setCell4((short)0);

        final List<Future<Server>> futures = new ArrayList<>();
        for (int i = 1; i <= 256; i++) {
            futures.add(ipIsOpen(es, ip.toString(), Constants.SERVER_PORT, timeout));
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

        Log.d(TAG, "Number of servers found: " + openPorts);
        return serverIpsList;
    }

    public static Future<Server> ipIsOpen(final ExecutorService es, final String ip, final int port, final int timeout) {

        //Log.v(TAG, "Checking if ip is open: " + ip + ":" + port);

        return es.submit(new IpChecker(ip, port, timeout));
//        return es.submit(new Callable<Boolean>() {
//            @Override public Boolean call() {
//
//
////                try {
////                    SocketAddress sockaddr = new InetSocketAddress(ip, port);
////                    // Create an unbound socket
////                    Socket sock = new Socket();
////                    sock.connect(sockaddr, timeout);
////                    Log.v(TAG, "ip is opened:" + ip + ":" + port);
////                    return true;
////                } catch (IOException e) {
////                    Log.v(TAG, "ip is closed(IOException): " + ip + ":" + port);
////                    return false;
////                }
//
//                try {
//                    URL url = new URL("http://" + ip + ":" + port);
//                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                    urlConnection.setConnectTimeout(timeout);
//                    urlConnection.connect();
//                    Log.v(TAG, "ip is opened:" + ip + ":" + port);
//                    urlConnection.disconnect();
//
//                    return true;
//                } catch (MalformedURLException e) {
//                    Log.v(TAG, "ip is closed(MalformedURLException): " + ip + ":" + port);
//                    return false;
//                } catch (IOException e) {
//                    Log.v(TAG, "ip is closed(IOException): " + ip + ":" + port);
//                    return false;
//                }
//
//
////                try {
////                    Socket socket = new Socket();
////                    socket.connect(new InetSocketAddress(ip, port), timeout);
////                    socket.close();
////                    Log.v(TAG, "ip is opened:" + ip + ":" + port);
////                    return true;
////                } catch (Exception ex) {
////                    Log.v(TAG, "ip is closed: " + ip + ":" + port);
////                    return false;
////                }
//            }
//        });
    }
    public IPv4 getIpAddress() {
//        try {
//            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
//                NetworkInterface intf = en.nextElement();
//                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
//                    InetAddress inetAddress = enumIpAddr.nextElement();
//                    if (!inetAddress.isLoopbackAddress()) {
//                        return inetAddress.getHostAddress().toString();
//                    }
//                }
//            }
//        } catch (SocketException ex) {
//            ex.printStackTrace();
//        }
//        return null;

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        return new IPv4((ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
}

    @Override
    public boolean stopService(Intent name) {
        Log.d(TAG, "Stopping service");
        return super.stopService(name);
    }
}
