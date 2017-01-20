package com.gmail.btheo95.aria.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.btheo95.aria.R;
import com.gmail.btheo95.aria.model.IpCheckerContext;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link IpCheckerContext} and makes a call to the
 ///////////
 * TODO: Replace the implementation with code for your data type.
 */
///////////* specified {@link OnListFragmentInteractionListener}.


public class ServerRecyclerViewAdapter extends RecyclerView.Adapter<ServerRecyclerViewAdapter.ViewHolder> {

    private final List<IpCheckerContext> mValues;
//    private final OnListFragmentInteractionListener mListener;

//    public ServerRecyclerViewAdapter(List<IpCheckerContext> items, OnListFragmentInteractionListener listener) {
    public ServerRecyclerViewAdapter(List<IpCheckerContext> items) {
        mValues = items;
//        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_servers, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mServerIp.setText(mValues.get(position).getIp());
        holder.mServerName.setText(mValues.get(position).getDeviceName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        public IpCheckerContext mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mServerIp = (TextView) view.findViewById(R.id.serverIp);
            mServerName = (TextView) view.findViewById(R.id.serverIp);
        }

    }
}
