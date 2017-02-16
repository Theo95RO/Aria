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
    private final List<Server> mData;
    private Server mDefaultServer;

    private RadioButton lastChecked = null;
    private int lastCheckedPosition = 0;

    private final RecyclerItemClickListener mListener;

    public ServerRecyclerViewAdapter(List<Server> items, Server defaultServer, RecyclerItemClickListener listener) {
        mData = items;
        mDefaultServer = defaultServer;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_servers, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mData.get(position);
        holder.mServerIp.setText(mData.get(position).getIp());
        holder.mServerName.setText(mData.get(position).getDeviceName());

        if (mDefaultServer != null && mDefaultServer.equals(mData.get(position))) {
            holder.mRadioButton.setChecked(true);
            lastChecked = holder.mRadioButton;
            lastCheckedPosition = position;
        } else {
            holder.mRadioButton.setChecked(false);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleHolderClick(holder, position);
            }
        });

        holder.mRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleHolderClick(holder, position);
            }
        });

        Log.v(TAG, "Clicked on server number: " + position);
    }


    private void handleHolderClick(final ViewHolder holder, final int position) {

        Log.d(TAG, "Clicked on position: " + position);
        Log.d(TAG, "LayoutPosition: " + holder.getLayoutPosition());
        Log.d(TAG, "AdapterPosition: " + holder.getAdapterPosition());
        if (position == lastCheckedPosition) {
            return;
        }

        if (lastChecked != null) {
            lastChecked.setChecked(false);
        }

        holder.mRadioButton.setChecked(true);
        lastChecked = holder.mRadioButton;
        lastCheckedPosition = position;

        if (null != mListener) {
            mListener.onRecyclerItemClick(holder.mItem);
        }

        setDefaultServer(holder.mItem);
    }

    @Override
    public int getItemCount() {
        return mData.size();
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

    // Clean all elements of the recycler
    public void clearData() {
        mData.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAllToData(List<Server> list) {
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void changeData(List<Server> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void setDefaultServer(Server server) {
        mDefaultServer = server;
    }

    public interface RecyclerItemClickListener {
        void onRecyclerItemClick(Server item);
    }
}
