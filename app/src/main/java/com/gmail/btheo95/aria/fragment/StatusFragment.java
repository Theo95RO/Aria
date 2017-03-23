package com.gmail.btheo95.aria.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.gmail.btheo95.aria.R;
import com.gmail.btheo95.aria.service.MediaJobService;
import com.gmail.btheo95.aria.utils.Database;
import com.gmail.btheo95.aria.utils.Media;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class StatusFragment extends Fragment {

    private static final int CHART_DATA_UPDATED_MESSAGE = 0;
    private static final String TAG = StatusFragment.class.getSimpleName();

    private Context mContext;
    private Database mDatabase;

    private PieChart mChart;
    private ScheduledExecutorService mScheduler;

    private Handler mHandler;

    public StatusFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        StatusFragment fragment = new StatusFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mDatabase = new Database(context);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialiseHandler();
        MediaJobService.restartNewMediaJob(mContext.getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_status, container, false);

        mChart = (PieChart) mainView.findViewById(R.id.chart);
        mChart.setDescription(null);
        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mScheduler == null || mScheduler.isShutdown()) {
            mScheduler = Executors.newScheduledThreadPool(1);
        }
        mScheduler.scheduleWithFixedDelay(new StatusFragment.DataUpdater(CHART_DATA_UPDATED_MESSAGE), 0, 1, TimeUnit.SECONDS);

    }

    @Override
    public void onStop() {
        mScheduler.shutdown();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mScheduler.shutdownNow();
        super.onDestroy();
    }

    private void initialiseHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
//                if (null == StatusFragment.this) {
//                    return;
//                }

                switch (msg.what) {
                    case CHART_DATA_UPDATED_MESSAGE:
                        mChart.setData((PieData) msg.obj);
                        mChart.invalidate();
                    default:
                        break;
                }
            }
        };
    }

    private class DataUpdater implements Runnable {

        private final int mMessage;

        private DataUpdater(int message) {
            this.mMessage = message;
        }

        @Override
        public void run() {
            PieEntry[] entries = new PieEntry[]{
                    new PieEntry(mDatabase.getRemovedFilesCount(), "uploaded"),
                    new PieEntry(Media.getMediaToBeUploadedCount(mContext), "to be uploaded")
            };

            PieDataSet dataSet = new PieDataSet(Arrays.asList(entries), "# of photos");
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

            PieData pieData = new PieData(dataSet);

            Message message = new Message();
            message.what = mMessage;
            message.obj = pieData;
            mHandler.sendMessage(message);
        }
    }
}


