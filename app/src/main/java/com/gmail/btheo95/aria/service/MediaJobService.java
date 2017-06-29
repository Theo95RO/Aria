package com.gmail.btheo95.aria.service;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.gmail.btheo95.aria.R;
import com.gmail.btheo95.aria.utils.Media;
import com.gmail.btheo95.aria.utils.MediaUploader;
import com.gmail.btheo95.aria.utils.Permissions;
import com.permissioneverywhere.PermissionEverywhere;
import com.permissioneverywhere.PermissionResponse;

import java.util.List;

/**
 * Created by btheo on 05.03.2017.
 */

public class MediaJobService extends JobService {

    public static final String TAG = MediaJobService.class.getSimpleName();

    public static final int JOB_NEW_MEDIA = 0;
    private Worker mWorker;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "MediaJobService.onStartJob()");
        mWorker = new Worker(jobParameters);
        new Thread(mWorker).start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "MediaJobService.onStopJob()");
        mWorker.stop();
        return true;
    }

    public static void startNewMedia(Context context) {
        ComponentName service = new ComponentName(context, MediaJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(JOB_NEW_MEDIA, service)
//                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPeriodic(3600 * 1000) // 10 minutes - 600000
                .setBackoffCriteria(60 * 1000, JobInfo.BACKOFF_POLICY_LINEAR)
                .setRequiresDeviceIdle(false)
                .setRequiresCharging(false)
                .setPersisted(true)
                .build();

        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int result = scheduler.schedule(jobInfo);
        if (result == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled successfully!");
        }
    }

    public static void startNewMediaJobIfNotPending(Context context) {
        if (isJobPending(context, JOB_NEW_MEDIA)) {
            return;
        }
        startNewMedia(context);
    }

    public static void restartNewMediaJob(Context context) {
        stopNewMediaJob(context);
        startNewMedia(context);
    }

    public static void stopNewMediaJob(Context context) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.cancel(JOB_NEW_MEDIA);
    }

    public static boolean isJobPending(Context context, int id) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        List<JobInfo> pendingJobs = scheduler.getAllPendingJobs();

        for (JobInfo job : pendingJobs) {
            if (job.getId() == id) {
                return true;
            }
        }
        return false;
    }

    private class Worker implements Runnable {

        private JobParameters mJobParameters;
        private MediaUploader mMediaUploader;

        Worker(JobParameters jobParameters) {
            this.mJobParameters = jobParameters;
            Log.d(TAG, "Worker()");
        }

        @Override
        public void run() {
            Context context = getApplicationContext();
            if (!arePermissionGranted(context)) {
                return;
            }
            if (!isConnectedToWifi(context)) {
                return;
            }
            Media.updateData(context);
            mMediaUploader = new MediaUploader(context);
            mMediaUploader.startUploading();
            MediaJobService.this.jobFinished(mJobParameters, false);
        }

        private boolean arePermissionGranted(Context context) {
            PermissionResponse response = null;
            try {
                response = PermissionEverywhere.getPermission(getApplicationContext(),
                        Permissions.allPermissions,
                        0,
                        context.getString(R.string.notification_permissions_title),
                        context.getString(R.string.notification_permissions_content),
                        R.drawable.ic_error_outline_black_24dp)
                        .call();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }

            if (response == null) {
                return false;
            }
            //waits...
            return response.isGranted();
        }

        void stop() {
            if (mMediaUploader == null) {
                return;
            }
            mMediaUploader.stop();
        }
    }

    private boolean isConnectedToWifi(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if (info != null &&
                info.isConnected() &&
                info.getType() == ConnectivityManager.TYPE_WIFI) {

            return true;

        } else {
            return false;
        }
    }
}
