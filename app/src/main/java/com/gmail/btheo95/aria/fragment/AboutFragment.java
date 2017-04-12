package com.gmail.btheo95.aria.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.btheo95.aria.R;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;


public class AboutFragment extends Fragment {

    private AboutFragment.OnFragmentInteractionListener mListener;

    public AboutFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View contextView = inflater.inflate(R.layout.fragment_about, container, false);
        Context mContext = contextView.getContext();

        return new AboutPage(mContext)
                .isRTL(false) // right to left
                .setDescription(getString(R.string.about_description, getString(R.string.app_name)))
                .setImage(R.drawable.ic_help_black_24dp)
                .addItem(new Element().setTitle(getString(R.string.about_version)))
                .addGroup(getString(R.string.about_connect_with_us_section))
                .addEmail("b.theo95@gmail.com")
                .addWebsite("http://medyo.github.io/")
                .addPlayStore("com.ideashower.readitlater.pro")
                .addGroup(getString(R.string.about_copyright_section))
                .addItem(getCopyRightsElement())
                .addItem(getLicenseElement())
                .create();
    }

    private Element getLicenseElement() {
        Element copyLicenseElement = new Element();

        copyLicenseElement.setTitle(getString(R.string.about_license));
        copyLicenseElement.setIcon(R.drawable.ic_assignment_black_24dp);
        copyLicenseElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onLicenseClicked();
            }
        });

        return copyLicenseElement;
    }

    private Element getCopyRightsElement() {

        Element copyRightsElement = new Element();

        copyRightsElement.setTitle(getString(R.string.about_copyright, Calendar.getInstance().get(Calendar.YEAR)));
        copyRightsElement.setIcon(R.drawable.ic_copyright_black_24dp);

        return copyRightsElement;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onLicenseClicked();
    }
}
