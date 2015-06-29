package com.totirrapp.cc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DetailsFragmentCountUp extends Fragment{

    public DetailsFragmentCountUp() {}

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_details_count_up,container, false);
        return rootView;
    }


}