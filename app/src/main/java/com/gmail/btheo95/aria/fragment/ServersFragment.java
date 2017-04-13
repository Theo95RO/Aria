package com.gmail.btheo95.aria.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.btheo95.aria.R;
import com.gmail.btheo95.aria.adapter.ServerRecyclerViewAdapter;
import com.gmail.btheo95.aria.model.Server;
import com.gmail.btheo95.aria.network.Network;
import com.gmail.btheo95.aria.utils.Database;
import com.gmail.btheo95.aria.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A fragment representing a list of Items.
 */
public class ServersFragment extends Fragment implements ServerRecyclerViewAdapter.RecyclerItemClickListener {

    private static final String TAG = ServersFragment.class.getSimpleName();

    private Context mContext;
    private Database mDatabase;
    private Handler mHandler;

    private ServerRecyclerViewAdapter mAdapter;
    private List<Server> mListOfServers;
    private ScheduledExecutorService mScheduler;

    private LinearLayout mLoadingContainer;
    private GridLayout mInexistentServerContainer;

    private LinearLayout mListContainer;
    private boolean mActivityIsRunning;
    private final static int SERVERS_LIST_UPDATED_MESSAGE = 0;
    private final static int INEXISTENT_SERVER_MESSAGE = 2;
    private boolean mIsInexistentServersLayoutSet = false;
    private boolean mIsServersListLayoutSet = false;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ServersFragment() {
    }

    public static Fragment newInstance() {
        return new ServersFragment();
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
        mScheduler.scheduleWithFixedDelay(new ServersSearcher(), 0, 5, TimeUnit.SECONDS);
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
        if (mScheduler != null) {
            mScheduler.shutdownNow();
        }
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_servers_list, container, false);

        mDatabase = new Database(mContext);
        mAdapter = new ServerRecyclerViewAdapter(mListOfServers, mDatabase.getServer(), this);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.servers_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(mAdapter);

        mLoadingContainer = (LinearLayout) view.findViewById(R.id.servers_loading_container);
        mListContainer = (LinearLayout) view.findViewById(R.id.servers_list_container);
        mListContainer.setVisibility(View.GONE);
        mInexistentServerContainer = (GridLayout) view.findViewById(R.id.fragment_about_inexistent_server_layout);
        mInexistentServerContainer.setVisibility(View.GONE);

        Button copyButton = (Button) view.findViewById(R.id.fragment_servers_button_copy);
        Button shareButton = (Button) view.findViewById(R.id.fragment_servers_button_share);

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCopyButtonClick();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleShareButtonClick();
            }
        });

        TextView serverUrlTextView = (TextView) view.findViewById(R.id.fragment_servers_url);
        String serverUrlString = getString(R.string.server_download_url);
        SpannableString spannableString = new SpannableString(serverUrlString);
        spannableString.setSpan(new RelativeSizeSpan(4f), 0, serverUrlString.length(), 0); // set size
        serverUrlTextView.setText(spannableString);

        instantiateHandler();

        return view;
    }

    private void handleCopyButtonClick() {
        Utils.copyToClipboard(mContext, getString(R.string.server_download_url), getString(R.string.server_download_url));
        Toast.makeText(mContext, "URL Copied", Toast.LENGTH_SHORT).show(); //TODO: get string from resources
    }

    private void handleShareButtonClick() {
        Utils.startShareIntent(mContext, getString(R.string.intro_2_intent_share_url_title), getString(R.string.server_download_http_url));
    }


    @Override
    public void onAttach(Context context) {
        this.mContext = context;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach()");
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
//
//                    case INITIAL_SERVERS_LIST_MESSAGE:
//                        Log.d(TAG, "INITIAL_SERVERS_LIST_MESSAGE");
//                        changeViewContainers();

                    case SERVERS_LIST_UPDATED_MESSAGE:

                        Log.d(TAG, "SERVERS_LIST_UPDATED_MESSAGE");
                        mAdapter.changeData((List<Server>) msg.obj);

                        if (!mIsServersListLayoutSet) {
                            displayServersListLayout();
                            mIsServersListLayoutSet = true;
                            mIsInexistentServersLayoutSet = false;
                        }
                        break;

                    case INEXISTENT_SERVER_MESSAGE:
                        if (!mIsInexistentServersLayoutSet) {
                            displayInexistentServersLayout();
                            mIsInexistentServersLayoutSet = true;
                        }
                        break;

                    default:
                        break;
                }
            }
        };
    }

    private void displayServersListLayout() {
        final int shortAnimationTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        final int yTranslation = 120;
        List<Animator> animationsList = new ArrayList<>();

        if (mLoadingContainer.getVisibility() == View.VISIBLE) {
            animationsList.addAll(getFlyOutAnimatorsForView(mLoadingContainer));
        }

        if (mInexistentServerContainer.getVisibility() == View.VISIBLE) {
            ObjectAnimator animatorAlphaInexistentServerContainer = ObjectAnimator.ofFloat(mInexistentServerContainer, "alpha", 1f, 0f);
            animatorAlphaInexistentServerContainer.setDuration(shortAnimationTime);
            animatorAlphaInexistentServerContainer.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mInexistentServerContainer.setVisibility(View.GONE);
                }
            });
            animationsList.add(animatorAlphaInexistentServerContainer);
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animationsList);
        animatorSet.start();

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mListContainer.setY(yTranslation);
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

    private List<Animator> getFlyOutAnimatorsForView(final View view) {
        final int shortAnimationTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        List<Animator> animationsList = new ArrayList<>();

        ObjectAnimator animatorAlpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        animatorAlpha.setDuration(shortAnimationTime);

        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "y", view.getY(), view.getY() - (view.getHeight() / 2));
        animatorY.setDuration(shortAnimationTime);
        animatorY.setInterpolator(new AnticipateInterpolator());
        animatorY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });

        animationsList.add(animatorAlpha);
        animationsList.add(animatorY);

        return animationsList;
    }

    private void displayInexistentServersLayout() {
        final int shortAnimationTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        ObjectAnimator animatorLoadingContainer = ObjectAnimator.ofFloat(mLoadingContainer, "alpha", 1f, 0f, 1f);
        animatorLoadingContainer.setDuration(2 * shortAnimationTime);

        ObjectAnimator animatorInexistentServerContainer = ObjectAnimator.ofFloat(mInexistentServerContainer, "alpha", 0f, 1f);
        animatorInexistentServerContainer.setStartDelay(shortAnimationTime);
        animatorInexistentServerContainer.setDuration(shortAnimationTime);
        animatorInexistentServerContainer.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mInexistentServerContainer.setAlpha(0f);
                mInexistentServerContainer.setVisibility(View.VISIBLE);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorLoadingContainer, animatorInexistentServerContainer);
        animatorSet.start();
    }

//    private void changeViewContainers() {
//        final int shortAnimationTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
//        final int translationYValue = 120;
//        mLoadingContainer.animate()
//                .translationYBy(-translationYValue)
//                .alpha(0.0f)
//                .setDuration(shortAnimationTime)
//                .setInterpolator(new AnticipateInterpolator())
//                .setListener(new AnimatorListenerAdapter() {
//
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        super.onAnimationEnd(animation);
//                        mLoadingContainer.setVisibility(View.GONE);
//                        mListContainer.setY(translationYValue);
//
//                        mListContainer.setAlpha(0.0f);
//                        mListContainer.setVisibility(View.VISIBLE);
//                        mListContainer.animate()
//                                .translationY(0)
//                                .alpha(1.0f)
//                                .setDuration(shortAnimationTime)
//                                .setInterpolator(new FastOutSlowInInterpolator());
//
//                    }
//                });
//    }

    @Override
    public void onRecyclerItemClick(Server item) {
        //TODO: restart service
        mDatabase.setServer(item);
    }

    private class ServersSearcher implements Runnable {

        ServersSearcher() {
        }

        @Override
        public void run() {
            Log.v(TAG, "started searching for servers");
            List<Server> updatedListOfServers = Network.getLocalServersList(getActivity());
            Log.v(TAG, "stopped searching for servers");

            Server defaultServer = mDatabase.getServer();
            int what = SERVERS_LIST_UPDATED_MESSAGE;

            if (defaultServer == null) {
                if (updatedListOfServers.size() == 0) {
                    what = INEXISTENT_SERVER_MESSAGE;
                }
//                if (updatedListOfServers.size() >= 1) {
//                    defaultServer = updatedListOfServers.get(0);
//                    mDatabase.setServer(defaultServer);
//                    mAdapter.setDefaultServer(defaultServer);
//                } else {
//                    what = INEXISTENT_SERVER_MESSAGE;
//                }
            } else {
                if (!updatedListOfServers.contains(defaultServer)) {
                    updatedListOfServers.add(0, defaultServer);
                }
            }

            Message message = new Message();
            message.what = what;
            message.obj = updatedListOfServers;
            mHandler.sendMessage(message);
        }
    }

}
