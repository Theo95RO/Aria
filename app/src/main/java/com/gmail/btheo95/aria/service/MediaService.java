package com.gmail.btheo95.aria.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gmail.btheo95.aria.model.Server;
import com.gmail.btheo95.aria.utils.Constants;
import com.gmail.btheo95.aria.utils.Database;
import com.gmail.btheo95.aria.utils.Media;
import com.gmail.btheo95.aria.utils.MediaUploader;
import com.gmail.btheo95.aria.utils.Utils;
import com.jaredrummler.android.device.DeviceName;

public class MediaService extends IntentService {

    private static final String TAG = MediaService.class.getSimpleName();

    private static final String PATH_PARAM = "path";

    private static final String ACTION_SEARCH_MEDIA = "action_search";
    private static final String ACTION_UPLOAD_MEDIA = "action_upload";
    private static final String ACTION_UPLOAD_CUSTOM_FILE = "action_upload_custom_file";

    private Database db;

    public MediaService() {
        super("MediaService");
        db = new Database(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "MediaService.onHandleIntent()");
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
        MediaUploader uploader = new MediaUploader(getApplicationContext());
        uploader.startUploading();
    }

//    private void handleActionUploadMedia() {
//        Server server = db.getServer();
//        if (null == server) {
//            return;
//        }
//
//        List<File> filesToUpload = db.getMedia();
//
//        String serverURL = getServerURL(server);
//        HttpFileUpload httpFileUpload;
//
//        try {
//            httpFileUpload = new HttpFileUpload(serverURL);
//            for (File file : filesToUpload) {
//                try {
//                    httpFileUpload.sendNow(file);
//                    db.removeFile(file);
//                    Log.v(TAG, "1 file uploaded");
//                } catch (IOException e) {
//                    Log.v(TAG, "1 file upload failed");
//                }
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//
//
//    }

    private String getServerURL(Server server) {
        String deviceNameAndIMEI = DeviceName.getDeviceName() + " - " + Utils.getDeviceImei(this);
        String serverURL = ("http://" + server.getIp() + ":" + Constants.SERVER_PORT + "/uploadPhoto/" + deviceNameAndIMEI);
        serverURL = serverURL.replaceAll(" ", "_");
        return serverURL;
    }

    private void handleActionSearchMedia() {
        Media.updateData(getApplicationContext());
    }

    // TODO: Check permissions
    public static void start(Context context) {
        startSearchingNewMedia(context);
        startUploadingMedia(context);
    }

    public static void startSearchingNewMedia(Context context) {
        Intent intent = new Intent(context, MediaService.class);
        intent.setAction(ACTION_SEARCH_MEDIA);
        context.startService(intent);
    }

    public static void startUploadingMedia(Context context) {
        Intent intent = new Intent(context, MediaService.class);
        intent.setAction(ACTION_UPLOAD_MEDIA);
        context.startService(intent);
    }

    public static void startUploadingCustomFile(Context context, String path) {
        Intent intent = new Intent(context, MediaService.class);
        intent.setAction(ACTION_UPLOAD_CUSTOM_FILE);
        intent.putExtra(PATH_PARAM, path);
        context.startService(intent);
    }
}