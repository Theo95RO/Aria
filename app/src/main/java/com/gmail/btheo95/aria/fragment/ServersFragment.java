package com.gmail.btheo95.aria.fragment;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.btheo95.aria.R;
import com.gmail.btheo95.aria.model.IpCheckerContext;
import com.gmail.btheo95.aria.model.Server;
import com.gmail.btheo95.aria.utils.Network;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ServersFragment extends Fragment {

    private static final String TAG = ServersFragment.class.getSimpleName();

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ServersFragment() {
    }

//    // TODO: Customize parameter initialization
//    @SuppressWarnings("unused")
//    public static ServersFragment newInstance(int columnCount) {
//        ServersFragment fragment = new ServersFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_COLUMN_COUNT, columnCount);
//        fragment.setArguments(args);
//        return fragment;
//    }

    public static Fragment newInstance() {
        ServersFragment fragment = new ServersFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        inflater.inflate(R.layout.fragment_servers_above, container, false);
        View view = inflater.inflate(R.layout.fragment_servers_list, container, false);


        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

//            List<Server> testList = new ArrayList<>();
//            Server ipc1 = new Server("100.5", 1, "nume", true, "mac adress");
//            Server ipc2 = new Server("222.5", 1, "nume2", true, "mac adress");
//            Server ipc3 = new Server("333.5", 1, "nume3", true, "mac adress");
//            testList.add(ipc1);
//            testList.add(ipc2);
//            testList.add(ipc3);





            Log.d(TAG, "started searching for servers");
            List<Server> testList2 = Network.getLocalServersList(getActivity());
            Log.d(TAG, "stopped searching for servers");

            recyclerView.setAdapter(new ServerRecyclerViewAdapter(testList2));
//            recyclerView.setAdapter(new ServerRecyclerViewAdapter(DummyContent.ITEMS, mListener));
        }
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
        // TODO: Update argument type and name
        void onListFragmentInteraction(Server item);
    }
}
