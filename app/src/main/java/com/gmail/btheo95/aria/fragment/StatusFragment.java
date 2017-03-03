package com.gmail.btheo95.aria.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.gmail.btheo95.aria.R;
import com.gmail.btheo95.aria.utils.Database;

import java.util.Arrays;


public class StatusFragment extends Fragment {

    private Database mDatabase;
    private Context mContext;

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
        mDatabase = new Database(mContext);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_status, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        PieChart chart = (PieChart) getView().findViewById(R.id.chart);

        PieEntry[] entries = new PieEntry[]{
                new PieEntry(mDatabase.getRemovedFilesCount(), "uploaded"),
                new PieEntry(mDatabase.getNumberOfFilesToBeUploaded(), "to be uploaded")
        };

        PieDataSet dataSet = new PieDataSet(Arrays.asList(entries), "# of photos");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData pieData = new PieData(dataSet);
        chart.setData(pieData);
    }
}
