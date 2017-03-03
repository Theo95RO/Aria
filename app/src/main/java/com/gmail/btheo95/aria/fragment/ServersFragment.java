package com.gmail.btheo95.aria.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.widget.LinearLayout;

import com.gmail.btheo95.aria.R;
import com.gmail.btheo95.aria.adapter.ServerRecyclerViewAdapter;
import com.gmail.btheo95.aria.model.Server;
import com.gmail.btheo95.aria.network.Network;
import com.gmail.btheo95.aria.utils.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ServersFragment extends Fragment implements ServerRecyclerViewAdapter.RecyclerItemClickListener {

    private static final String TAG = ServersFragment.class.getSimpleName();

    private Database mDatabase;
    private Handler mHandler;

    private ServerRecyclerViewAdapter mAdapter;
    private List<Server> mListOfServers;
    private ScheduledExecutorService mScheduler;
    private OnListFragmentInteractionListener mListener;

    private LinearLayout mLoadingContainer;
    private LinearLayout mListContainer;

    private boolean mActivityIsRunning;
    private final static int INITIAL_SERVERS_LIST_MESSAGE = 1;
    private final static int SERVERS_LIST_UPDATED_MESSAGE = 0;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ServersFragment() {
    }

    public static Fragment newInstance() {
        ServersFragment fragment = new ServersFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScheduler = Executors.newScheduledThreadPool(1); // Should I put it in constructor?
        mListOfServers = new ArrayList<>();

    }


    @Override
    public void onStart() {
        super.onStart();

        mActivityIsRunning = true;

        if (mScheduler == null || mScheduler.isShutdown()) {
            mScheduler = Executors.newScheduledThreadPool(1);
        }
        mScheduler.scheduleWithFixedDelay(new ServersSearcher(SERVERS_LIST_UPDATED_MESSAGE), 0, 5, TimeUnit.SECONDS);
        Log.d(TAG, "onStart()");
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop()");
        mActivityIsRunning = false;
        mScheduler.shutdown();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        mScheduler.shutdownNow();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_servers_list, container, false);
        Context context = view.getContext();

        mDatabase = new Database(context);
        mAdapter = new ServerRecyclerViewAdapter(mListOfServers, mDatabase.getServer(), this);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.servers_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mAdapter);

        mLoadingContainer = (LinearLayout) view.findViewById(R.id.servers_loading_container);
        mListContainer = (LinearLayout) view.findViewById(R.id.servers_list_container);
        mListContainer.setVisibility(View.GONE);

        instantiateHandler();

//        AVLoadingIndicatorView avi = (AVLoadingIndicatorView) view.findViewById(R.id.avi);
//        avi.hide();

        mScheduler.submit(new ServersSearcher(INITIAL_SERVERS_LIST_MESSAGE));
        return view;
    }


    @Override
    public void onAttach(Context context) {

        Log.d(TAG, "onAttach()");
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            //TODO: resolve lower comment
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
        }
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach()");
        mListener = null;
        super.onDetach();
    }


    private void instantiateHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (!mActivityIsRunning) {
                    return;
                }

                switch (msg.what) {

                    case INITIAL_SERVERS_LIST_MESSAGE:
                        changeViewContainers();

                    case SERVERS_LIST_UPDATED_MESSAGE:
//                        Log.d(TAG, "updateing list of servers");
//                        mListOfServers.clear();
//                        mListOfServers.addAll((Collection<? extends Server>) msg.obj);
//                        Log.d(TAG, "number of items in new list: " + ((Collection<? extends Server>) msg.obj).size());
//                        mAdapter.notifyDataSetChanged();
                        mAdapter.changeData((List<Server>) msg.obj);
                        break;

                    default:
                        break;
                }
            }
        };
    }

    private void changeViewContainers() {
        final int shortAnimationTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        final int translationYValue = 120;
        mLoadingContainer.animate()
                .translationYBy(-translationYValue)
                .alpha(0.0f)
                .setDuration(shortAnimationTime)
                .setInterpolator(new AnticipateInterpolator())
                .setListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mLoadingContainer.setVisibility(View.GONE);
                        mListContainer.setY(translationYValue);

                        mListContainer.setAlpha(0.0f);
                        mListContainer.setVisibility(View.VISIBLE);
                        mListContainer.animate()
                                .translationY(0)
                                .alpha(1.0f)
                                .setDuration(shortAnimationTime)
                                .setInterpolator(new FastOutSlowInInterpolator());

                    }
                });
    }

    @Override
    public void onRecyclerItemClick(Server item) {
        mDatabase.setServer(item);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Server item);
    }


    public class ServersSearcher implements Runnable {

        private final int message;

        ServersSearcher(int message) {
            this.message = message;
        }

        @Override
        public void run() {
            Log.v(TAG, "started searching for servers");
            List<Server> updatedListOfServers = Network.getLocalServersList(getActivity());
            Log.v(TAG, "stopped searching for servers");

            Server dummy01 = new Server("dummy01", "dummy01", "dummy01", true, "dummy01");
            Server dummy02 = new Server("dummy02", "dummy02", "dummy02", true, "dummy02");
            Server dummy03 = new Server("dummy03", "dummy03", "dummy03", true, "dummy03");
            Server dummy04 = new Server("dummy04", "dummy04", "dummy06", true, "dummy04");
            Server dummy05 = new Server("dummy05", "dummy05", "dummy05", true, "dummy05");
            Server dummy06 = new Server("dummy06", "dummy06", "dummy06", true, "dummy06");
            updatedListOfServers.add(dummy01);
            updatedListOfServers.add(dummy02);
            updatedListOfServers.add(dummy03);
            updatedListOfServers.add(dummy04);
            updatedListOfServers.add(dummy05);
            updatedListOfServers.add(dummy06);

            Server defaultServer = mDatabase.getServer();
            Log.d(TAG, "DEFAULT Server: " + defaultServer.getIp());


            if (defaultServer == null) {
                if (updatedListOfServers.size() >= 1) {
                    defaultServer = updatedListOfServers.get(0);
                    mDatabase.setServer(defaultServer);
                    mAdapter.setDefaultServer(defaultServer);
                }
            } else {
                if (!updatedListOfServers.contains(defaultServer)) {
                    updatedListOfServers.add(0, defaultServer);
                }
            }

            Message message = new Message();
            message.what = this.message;
            message.obj = updatedListOfServers;
            mHandler.sendMessage(message);
        }
    }

}
