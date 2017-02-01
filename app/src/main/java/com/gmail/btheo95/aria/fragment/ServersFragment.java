package com.gmail.btheo95.aria.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gmail.btheo95.aria.Database;
import com.gmail.btheo95.aria.R;
import com.gmail.btheo95.aria.model.IpCheckerContext;
import com.gmail.btheo95.aria.model.Server;
import com.gmail.btheo95.aria.utils.Network;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Collection;
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
public class ServersFragment extends Fragment {

    private static final String TAG = ServersFragment.class.getSimpleName();

    private Database mDatabase;
    private Handler mHandler;

    private ServerRecyclerViewAdapter mAdapter;
    private List<Server> mListOfServers;
    private ScheduledExecutorService mScheduler;
    private OnListFragmentInteractionListener mListener;

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



        if (mScheduler == null || mScheduler.isShutdown()) {
            mScheduler = Executors.newScheduledThreadPool(1);
        }
        mScheduler.scheduleWithFixedDelay(new ServersSearcher(SERVERS_LIST_UPDATED_MESSAGE), 0, 5, TimeUnit.SECONDS);
        Log.d(TAG, "onStart()");
    }

    @Override
    public void onStop() {
        super.onStop();
        mScheduler.shutdown();
    }

    @Override
    public void onDestroy() {
        mScheduler.shutdownNow();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_servers_list, container, false);
        Context context = view.getContext();
        mDatabase = new Database(context);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.servers_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        mAdapter = new ServerRecyclerViewAdapter(mListOfServers, mDatabase.getServer());
        recyclerView.setAdapter(mAdapter);

//        AVLoadingIndicatorView avi = (AVLoadingIndicatorView) view.findViewById(R.id.avi);
//        avi.hide();
        final LinearLayout loadingContainer = (LinearLayout) view.findViewById(R.id.servers_loading_container);
        final LinearLayout listContainer = (LinearLayout) view.findViewById(R.id.servers_list_container);
        listContainer.setVisibility(View.GONE);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {

                    case INITIAL_SERVERS_LIST_MESSAGE:

                        final int shortAnimationTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
                        final int translationYValue = 120;
                        loadingContainer.animate()
                                .translationYBy(-translationYValue)
                                .alpha(0.0f)
                                .setDuration(shortAnimationTime)
                                .setListener(new AnimatorListenerAdapter() {

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        loadingContainer.setVisibility(View.GONE);
                                        listContainer.setY(translationYValue);

                                        listContainer.setAlpha(0.0f);
                                        listContainer.setVisibility(View.VISIBLE);
                                        listContainer.animate()
                                                .translationY(0)
                                                .alpha(1.0f)
                                                .setDuration(shortAnimationTime);

                                    }
                                });
                        //break;

                    case SERVERS_LIST_UPDATED_MESSAGE:
                        Log.d(TAG, "updateing list of servers");
                        mListOfServers.clear();
                        mListOfServers.addAll((Collection<? extends Server>) msg.obj);
                        Log.d(TAG, "number of items in new list: " + ((Collection<? extends Server>) msg.obj).size());
                        mAdapter.notifyDataSetChanged();
                        break;

                    default:
                        break;
                }
            }
        };



        mScheduler.submit(new ServersSearcher(INITIAL_SERVERS_LIST_MESSAGE));
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            //TODO: resolve lower comment
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

        public ServersSearcher(int message) {
            this.message = message;
        }

        @Override
        public void run() {
            Log.d(TAG, "started searching for servers");
            List<Server> updatedListOfServers = Network.getLocalServersList(getActivity());
            Log.d(TAG, "stopped searching for servers");
            Server defaultServer = mDatabase.getServer();
            if (defaultServer == null) {
                if (updatedListOfServers.size() >= 1) {
                    defaultServer = updatedListOfServers.get(0);
                    mDatabase.setServer(defaultServer);
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
