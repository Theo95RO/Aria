package com.gmail.btheo95.aria.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gmail.btheo95.aria.model.Server;
import com.gmail.btheo95.aria.network.HttpFileUpload;
import com.gmail.btheo95.aria.utils.Database;
import com.gmail.btheo95.aria.utils.Permissions;
import com.gmail.btheo95.aria.utils.Utils;

import java.io.File;
import java.io.IOException;

public class CustomFileUploadIntentService extends IntentService {

    private static final String TAG = CustomFileUploadIntentService.class.getSimpleName();
    private static final String ACTION_UPLOAD = "com.gmail.btheo95.aria.service.CustomFileUploadIntentService.action.UPLOAD";
    private static final String EXTRA_PATH = "com.gmail.btheo95.aria.service.CustomFileUploadIntentService.extra.PATH";

    public CustomFileUploadIntentService() {
        super("CustomFileUploadIntentService");
    }

    public static void startActionUpload(Context context, String path) {
        Intent intent = new Intent(context, CustomFileUploadIntentService.class);
        intent.setAction(ACTION_UPLOAD);
        intent.putExtra(EXTRA_PATH, path);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPLOAD.equals(action)) {
                final String path = intent.getStringExtra(EXTRA_PATH);
                handleActionUpload(path);
            }
        }
    }

    private void handleActionUpload(String path) {
        File file = new File(path);
        if (!Permissions.arePermissionGrantedInBackgroud(getApplicationContext())) {
            return;
        }

//        if (file == null || !file.exists()) {
//            //TODO: notification
//            return;
//        }

        //TODO: start notification

        Context context = getApplicationContext();
        Database db = new Database(context);
        Server server = db.getServer();

        try {
            String url = "http://" + server.getIp() + ":" + server.getPort() + "/uploadCustomFile/" + Utils.getDeviceImei(getApplicationContext());
            Log.d(TAG, url);
            Log.d(TAG, file.getPath());

            HttpFileUpload uploader = new HttpFileUpload(url);
            uploader.sendNow(file);
            //TODO: finish notification
        } catch (IOException e) {
            Log.d(TAG, "error");
            //TODO: error notification
        }
    }
}
