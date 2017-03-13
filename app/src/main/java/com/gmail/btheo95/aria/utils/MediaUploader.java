package com.gmail.btheo95.aria.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.gmail.btheo95.aria.R;
import com.gmail.btheo95.aria.model.Server;
import com.gmail.btheo95.aria.network.HttpFileUpload;
import com.gmail.btheo95.aria.network.Network;
import com.jaredrummler.android.device.DeviceName;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by btheo on 09.03.2017.
 */

public class MediaUploader {

    private static final String TAG = MediaUploader.class.getSimpleName();

    private Context mContext;
    private Database mDatabase;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotificationBuilder;
    private String mServerURL;

    private volatile boolean isStopped = false;

    private boolean mShouldUploadPhotos;
    private boolean mShouldUploadVideos;
    private boolean mShouldShowNotifications;
    private boolean mShouldFreeMemory;

    private int mFilesToUploadCounter = 0;
    private int mFilesUploadedCounter = 0;
    private int mFilesSuccefullyUploadedCounter = 0;
    private int mFilesFailedUploadCounter = 0;

    public MediaUploader(Context context) {
        this.mContext = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(context);
        mDatabase = new Database(context);
    }


    public void startUploading() {
        isStopped = false;
        Server server = mDatabase.getServer();
        if (null == server) {
            //TODO: notification to select server
            return;
        }

        if (!Network.isServerReacheble(server)) {
            notifyServerNotFound();
            return;
        }

        List<File> filesToUpload = mDatabase.getMedia();
        if (filesToUpload.size() == 0) {
            return;
        }

        initPreferences();
        mServerURL = getServerURL(server);

        if (mShouldUploadPhotos && mShouldUploadVideos) {
            uploadFiles(filesToUpload);
        } else if (mShouldUploadVideos) {
            List<File> videoFiles = Media.getVideoFiles(filesToUpload);
            uploadFiles(videoFiles);
        } else if (mShouldUploadPhotos) {
            List<File> photoFiles = Media.getPhotoFiles(filesToUpload);
            uploadFiles(photoFiles);
        }
    }

    private void notifyServerNotFound() {
        mNotificationBuilder
                .setSmallIcon(R.drawable.ic_highlight_off_black_24dp)
                .setContentTitle("DEBUG")
                .setContentText("Server not found")
                .setContentInfo("Info");

        mNotificationManager.notify(Constants.NOTIFICATION_UPLOADING, mNotificationBuilder.build());

    }

    private void uploadFiles(List<File> filesToUpload) {
        mFilesToUploadCounter = filesToUpload.size();
        initNotifForUpl();

        try {
            HttpFileUpload httpFileUpload = new HttpFileUpload(mServerURL);

            for (File file : filesToUpload) {
                if (mFilesUploadedCounter == 20) {
                    uploadFinished();
                    return;
                }
                if (isStopped) {
                    return;
                }
                mFilesUploadedCounter++;
                updateNotification();
                try {
                    android.util.Pair<Integer, String> uploadResponse = httpFileUpload.sendNow(file);
                    Log.v(TAG, "Upload response: " + uploadResponse.first + " - " + uploadResponse.second);
//                    deleteFileIfNecessary(file); //TODO: use in release
                    mDatabase.removeFile(file);
                    mFilesSuccefullyUploadedCounter++;
                    Log.v(TAG, "1 file uploaded");
                } catch (IOException e) {
                    mFilesFailedUploadCounter++;
                    Log.v(TAG, "1 file upload failed");
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } finally {
            uploadFinished();
            Log.d(TAG, "Files succeded to upload: " + mFilesSuccefullyUploadedCounter);
            Log.d(TAG, "Files failed to upload: " + mFilesFailedUploadCounter);
        }
    }

    private void deleteFileIfNecessary(File file) throws IOException {
        if (!mShouldFreeMemory) {
            return;
        }
        boolean result = file.delete();
        if (!result) {
            throw new IOException();
        }
    }

    private void initNotifForUpl() {
        mNotificationBuilder
                .setSmallIcon(R.drawable.ic_file_upload_black_24dp)
                .setOngoing(true)
                .setColor(ContextCompat.getColor(mContext, R.color.primary))
                .setContentTitle(mContext.getString(R.string.notification_uploading_now_title));
        //TODO: onClick -> open app with Status fragment
    }

    private void updateNotification() {

        if (!mShouldShowNotifications) {
            return;
        }

        mNotificationBuilder
                .setContentText(mContext.getString(R.string.notification_uploading_now_content, mFilesUploadedCounter, mFilesToUploadCounter))
                .setProgress(mFilesToUploadCounter, mFilesUploadedCounter, false);

        mNotificationManager.notify(Constants.NOTIFICATION_UPLOADING, mNotificationBuilder.build());
    }

    private void uploadFinished() {
        if (!mShouldShowNotifications) {
            return;
        }
        if (mFilesSuccefullyUploadedCounter == 0) {
            mNotificationBuilder
                    .setSmallIcon(R.drawable.ic_highlight_off_black_24dp)
                    .setContentTitle(mContext.getString(R.string.notification_connection_lost_title));
        } else {
            mNotificationBuilder
                    .setSmallIcon(R.drawable.ic_done_black_24dp)
                    .setContentTitle(mContext.getString(R.string.notification_upload_finished_title));
        }

        mNotificationBuilder
                .setContentText(mContext.getString(R.string.notification_upload_finished_content, mFilesSuccefullyUploadedCounter))
                .setOngoing(false)
                .setProgress(1, 1, false);

        mNotificationManager.notify(Constants.NOTIFICATION_UPLOADING, mNotificationBuilder.build());

    }

    private void initPreferences() {
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(mContext);
        mShouldUploadPhotos = sharedPreference.getBoolean("upload_photos_preference", true);
        mShouldUploadVideos = sharedPreference.getBoolean("upload_videos_preference", false);
        mShouldShowNotifications = sharedPreference.getBoolean("show_notification_preference", true);
        mShouldFreeMemory = sharedPreference.getBoolean("free_memory_after_upload_preference", false);

    }

    private String getServerURL(Server server) {
        String deviceNameAndIMEI = DeviceName.getDeviceName() + " - " + Utils.getDeviceImei(mContext);
        String serverURL = ("http://" + server.getIp() + ":" + Constants.SERVER_PORT + "/uploadPhoto/" + deviceNameAndIMEI);
        serverURL = serverURL.replaceAll(" ", "_");
        return serverURL;
    }

    public void stop() {
        isStopped = true;
        clearNotification();
    }

    private void clearNotification() {
        mNotificationManager.cancel(Constants.NOTIFICATION_UPLOADING);
    }
}
