package com.gmail.btheo95.aria.fragment;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.gmail.btheo95.aria.R;
import com.gmail.btheo95.aria.model.IpCheckerContext;
import com.gmail.btheo95.aria.model.Server;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link IpCheckerContext} and makes a call to the
 ///////////
 * TODO: Replace the implementation with code for your data type.
 */
///////////* specified {@link OnListFragmentInteractionListener}.


public class ServerRecyclerViewAdapter extends RecyclerView.Adapter<ServerRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = ServerRecyclerViewAdapter.class.getSimpleName();
    private final List<Server> mValues;
    private Server mDefaultServer;

    private static RadioButton lastChecked = null;
    private static int lastCheckedPosition = 0;

//    private final OnListFragmentInteractionListener mListener;

//    public ServerRecyclerViewAdapter(List<IpCheckerContext> items, OnListFragmentInteractionListener listener) {
    public ServerRecyclerViewAdapter(List<Server> items, Server defaultServer) {
        mValues = items;
        mDefaultServer = defaultServer;
//        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_servers, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mServerIp.setText(mValues.get(position).getIp());
        holder.mServerName.setText(mValues.get(position).getDeviceName());

        if (mDefaultServer != null && mDefaultServer.equals(mValues.get(position))) {
            holder.mRadioButton.setChecked(true);
            lastChecked = holder.mRadioButton;
            lastCheckedPosition = position;
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (position == lastCheckedPosition) {
                    return;
                }

                if (lastChecked != null) {
                    lastChecked.setChecked(false);
                }

                holder.mRadioButton.setChecked(true);
                lastChecked = holder.mRadioButton;
                lastCheckedPosition = position;

                Log.v(TAG, "Clicked on server number: " + position);

                // TODO: Sa implementez listenerul ?
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onListFragmentInteraction(holder.mItem);
//                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mServerIp;
        public final TextView mServerName;
        public final RadioButton mRadioButton;
        public Server mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mServerIp = (TextView) view.findViewById(R.id.serverIp);
            mServerName = (TextView) view.findViewById(R.id.serverName);
            mRadioButton = (RadioButton) view.findViewById(R.id.serverRadioButton);
        }

    }

    public void setmDefaultServer(Server server) {
        mDefaultServer = server;
    }
}
