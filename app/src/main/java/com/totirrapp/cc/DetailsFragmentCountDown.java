package com.totirrapp.cc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DetailsFragmentCountDown extends Fragment{


    public DetailsFragmentCountDown() {    }

	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.details_activity_count_down,container, false);
		return rootView;
	}

}