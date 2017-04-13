package com.gmail.btheo95.aria.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gmail.btheo95.aria.model.Server;
import com.gmail.btheo95.aria.network.HttpFileUpload;
import com.gmail.btheo95.aria.network.Network;
import com.gmail.btheo95.aria.utils.Constants;
import com.gmail.btheo95.aria.utils.Database;
import com.gmail.btheo95.aria.utils.Notifications;
import com.gmail.btheo95.aria.utils.Permissions;
import com.gmail.btheo95.aria.utils.Utils;
import com.jaredrummler.android.device.DeviceName;

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
        Context context = getApplicationContext();
        Database db = new Database(context);
        Server server = db.getServer();
        File file = new File(path);
        if (!Permissions.arePermissionGrantedInBackgroud(getApplicationContext())) {
            return;
        }

        if (server == null) {
            Notifications.showNoServerFoundNotification(context, Constants.NOTIFICATION_UPLOADING);
            return;
        }

        if (!Network.isDeviceConnectedToWifi(context)) {
            Notifications.showNotificationNoWifi(context, Constants.NOTIFICATION_CUSTOM_FILE_UPLOAD_ID);
            return;
        }

        if (!Network.isServerReacheble(server)) {
            Notifications.showNotificationServerNotReacheble(context, Constants.NOTIFICATION_CUSTOM_FILE_UPLOAD_ID);
            return;
        }

        Notifications.showNotificationUploadingCustomFile(context, Constants.NOTIFICATION_CUSTOM_FILE_UPLOAD_ID);

        try {
            String deviceNameAndIMEI = DeviceName.getDeviceName() + " - " + Utils.getDeviceImei(getApplicationContext());
            String url = "http://" + server.getIp() + ":" + server.getPort() + "/uploadCustomFile/" + deviceNameAndIMEI;
            url = url.replaceAll(" ", "_");

            Log.d(TAG, url);
            Log.d(TAG, file.getPath());

            HttpFileUpload uploader = new HttpFileUpload(url);
            uploader.sendNow(file);
            Notifications.showNotificationUploadingCustomFileFinished(context, Constants.NOTIFICATION_CUSTOM_FILE_UPLOAD_ID);

        } catch (IOException e) {
            Log.d(TAG, "error");
            Notifications.showNotificationUploadingCustomFileFailed(context, Constants.NOTIFICATION_CUSTOM_FILE_UPLOAD_ID);

        }
    }
}
