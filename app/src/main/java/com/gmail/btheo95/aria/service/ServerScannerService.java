package com.gmail.btheo95.aria.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gmail.btheo95.aria.model.Server;
import com.gmail.btheo95.aria.network.HttpFileUpload;
import com.gmail.btheo95.aria.utils.Constants;
import com.gmail.btheo95.aria.utils.Database;
import com.gmail.btheo95.aria.utils.Utils;
import com.jaredrummler.android.device.DeviceName;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;

public class ServerScannerService extends IntentService {

    private static final String TAG = ServerScannerService.class.getSimpleName();

    private static final String PATH_PARAM = "path";

    private static final String ACTION_SEARCH_MEDIA = "action_search";
    private static final String ACTION_UPLOAD_MEDIA = "action_upload";
    private static final String ACTION_UPLOAD_CUSTOM_FILE = "action_upload_custom_file";

    private Database db;

    public ServerScannerService() {
        super("ServerScannerService");
        db = new Database(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "in service");
        if (intent != null) {

            final String action = intent.getAction();
            switch (action) {
                case ACTION_SEARCH_MEDIA:
                    handleActionSearchMedia();
                    break;

                case ACTION_UPLOAD_MEDIA:
                    handleActionUploadMedia();
                    break;

                case ACTION_UPLOAD_CUSTOM_FILE:
                    final String path = intent.getStringExtra(PATH_PARAM);
                    handleActionUploadCustomFile(path);
                    break;

                default:
                    break;
            }
        }
        db.close();
    }

    private void handleActionUploadCustomFile(String path) {
        //TODO: implement method
    }

    private void handleActionUploadMedia() {
        Server server = db.getServer();
        if (null == server) {
            return;
        }

        File[] filesToUpload = db.getAllPhotos();
        String deviceNameAndImei = DeviceName.getDeviceName() + " - " + Utils.getDeviceImei(this);
        String serverUri = ("http://" + server.getIp() + ":" + Constants.SERVER_PORT + "/uploadPhoto/" + deviceNameAndImei);
        serverUri = serverUri.replaceAll(" ", "_");
        HttpFileUpload httpFileUpload;
        try {
            httpFileUpload = new HttpFileUpload(serverUri);
            for (File file : filesToUpload) {
                try {
                    httpFileUpload.sendNow(file);
                    db.removeFile(file);
                    Log.v(TAG, "1 file uploaded");
                } catch (IOException e) {
                    Log.v(TAG, "1 file upload failed");
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }

    private void handleActionSearchMedia() {
        try {
            Date lastDate = db.getLastDate();
            Log.d(TAG, "Last sync date: " + lastDate.toString());
            List<File> photosList = Utils.getPhotosAfterDate(lastDate, this);
            File[] photosArray = new File[0];
            if (photosList != null) {
                photosArray = new File[photosList.size()];
            }
            photosList.toArray(photosArray);
            db.addFiles(photosArray);
            db.updateDate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO: Check permissions
    public static void start(Context context) {
        startSearchingNewMedia(context);
        startUploadingMedia(context);
    }

    public static void startSearchingNewMedia(Context context) {
        Intent intent = new Intent(context, ServerScannerService.class);
        intent.setAction(ACTION_SEARCH_MEDIA);
        context.startService(intent);
    }

    public static void startUploadingMedia(Context context) {
        Intent intent = new Intent(context, ServerScannerService.class);
        intent.setAction(ACTION_UPLOAD_MEDIA);
        context.startService(intent);
    }

    public static void startUploadingCustomFile(Context context, String path) {
        Intent intent = new Intent(context, ServerScannerService.class);
        intent.setAction(ACTION_UPLOAD_CUSTOM_FILE);
        intent.putExtra(PATH_PARAM, path);
        context.startService(intent);
    }
}