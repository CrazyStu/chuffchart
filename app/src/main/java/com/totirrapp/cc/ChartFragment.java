package com.totirrapp.cc;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import static com.totirrapp.cc.R.id;
import static com.totirrapp.cc.R.id.timeDoneTextView;
import static com.totirrapp.cc.R.layout;


public class ChartFragment extends Fragment {
    public View rootView;
    private clickCallback call;
    private View.OnClickListener shortPress;
    private View.OnLongClickListener longPress;
    private TextView percent = null;
    private TextView timeDoneText = null;
    private TextView timeLeftText = null;
    private TextView titleBot = null;
    private String chartName = "no name";
    private String chartHeader = "I Can't Wait...";
    private String chartTitle = "no title";
    private String chartStart = "02/02/2002";
    private String chartEnd = "02/02/2020";
    private String chartBgUrl = "noURL";
//    private int  ARG_SECTION_NUMBER = 0;
    private int screenHeight = 999;
    private int screenWidth = 998;
    private CounterFragment counter;
    private int chartNo = 99;
    private ArrayList<String> values;

    public ChartFragment() {
        Log.d("#---Chart"+chartNo+"---#", "ChartFragment()");
    }

    @Override
    public void onAttach(Activity activity) {
//        Log.d("#---Chart"+chartNo+"---#", "onAttach()");
        super.onAttach(activity);
        try {
            call = (clickCallback) activity;
        } catch (ClassCastException e) {
            Log.d("#---Chart"+chartNo+"---#", "onAttach failed");
            e.printStackTrace();
        }
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Log.d("#---Chart"+chartNo+"---#", "onCreateView()");
        rootView = inflater.inflate(R.layout.frag_home, container, false);
        setupListeners();
        setupButtons();
        if (chartNo == 99) {
            getArgs();
        }
        call.initiateBG(chartNo);
        return rootView;
    }
    public void getArgs() {
//        Log.d("#---Chart"+chartNo+"---#", "getArgs()");
        chartNo = getArguments().getInt("ChartNo");
        screenHeight = getArguments().getInt("Height");
        screenWidth = getArguments().getInt("Width");
        readChartValues();
    }
    public void readChartValues() {
        Log.d("#---Chart"+chartNo+"---#", "readChartValues()");
        String y = "Chart" + chartNo + ">ReadChart()";
        try {
            values = databaseReader.getChartInfo(chartNo, y);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (values != null) {
            chartName = values.get(0);
            chartHeader = values.get(1);
            chartTitle = values.get(2);
            chartStart = values.get(3);
            chartEnd = values.get(4);
            chartBgUrl = values.get(5);
            counter = new CounterFragment(chartStart, chartEnd);
        }
    }
    public void setNewBackground(BitmapDrawable bmp) {
        try {
            rootView.setBackground(bmp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public interface clickCallback {
        void onShortPress(int v);

        void onLongPress(int v);

        void initiateBG(int x);
    }
    public void setupListeners() {
        shortPress = new View.OnClickListener() {
            public void onClick(View v) {
                Log.e("shortPress", "viewID =" + v.getId());
                call.onShortPress(v.getId());
//                v.setVisibility(View.VISIBLE);
            }
        };
        longPress = new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                Log.e("LOOOONGPress", "viewID =" + v.getId());
                call.onLongPress(v.getId());
//                v.setVisibility(View.INVISIBLE);
                return true;
            }
        };
    }
    public void setupButtons() {
        View timeDoneButton;
        timeDoneButton = this.rootView.findViewById(id.timeDoneTextView);
        timeDoneButton.setOnClickListener(shortPress);
        timeDoneButton.setOnLongClickListener(longPress);

        View timeLeftButton;
        timeLeftButton = this.rootView.findViewById(id.timeLeftTextView);
        timeLeftButton.setOnClickListener(shortPress);
        timeLeftButton.setOnLongClickListener(longPress);

        View chartTitle;
        chartTitle = this.rootView.findViewById(id.chart_title);
        chartTitle.setOnClickListener(shortPress);
        chartTitle.setOnLongClickListener(longPress);

        View progressBar;
        progressBar = this.rootView.findViewById(id.progBar);
        progressBar.setOnClickListener(shortPress);
        progressBar.setOnLongClickListener(longPress);

        View chartBackground;
        chartBackground = this.rootView.findViewById(id.frag_home_parent_view);
        chartBackground.setOnClickListener(shortPress);
        chartBackground.setOnLongClickListener(longPress);
    }
    public void updateChartView() {
        try {
            counter.updateCounter();
            int height=200;
			try{
				percent=(TextView)rootView.findViewById(R.id.percDoneText);
				timeDoneText=(TextView)rootView.findViewById(timeDoneTextView);
				timeLeftText=(TextView)rootView.findViewById(R.id.timeLeftTextView);
				titleBot=(TextView)rootView.findViewById(R.id.titleBot);
				height = (int) (screenHeight * ((float) counter.getPercentDone() / 100));
			}catch(Exception e){
				Log.d("#---Chart"+chartNo+"---#", "Failed to set chart variables)");
				e.printStackTrace();
			}
            rootView.findViewById(id.progBarBlue).setMinimumHeight(height);
            rootView.invalidate();
            percent.setText(counter.getPercentDone() + "%");
            if (counter.getDaysTotalDone() > 0) {
                timeDoneText.setText(counter.getDaysTotalDone() + " " + getString(R.string.daysDone));
            } else {
                timeDoneText.setText(counter.getHoursTotalDone() + " " + getString(R.string.hoursDone));
            }
            if (counter.getDaysTotalLeft() > 0) {
                timeLeftText.setText(counter.getDaysTotalLeft() + " " + getString(R.string.daysLeft));
            } else {
                timeLeftText.setText(counter.getHoursTotalLeft() + " " + getString(R.string.hoursLeft));
            }
			titleBot.setText(chartTitle);
        } catch (Exception e) {
			Log.d("#---Chart"+chartNo+"---#", "Failed to set update view)");
            e.printStackTrace();
        }

    }
    public String getChartName() {
        return chartName;
    }
    public String getChartHeader() {
        return chartHeader;
    }
    public String getChartTitle() {
        return chartTitle;
    }
    public String getChartStart() {
        return chartStart;
    }
    public String getChartEnd() {
        return chartEnd;
    }
    public String getChartBgUrl() {
        return chartBgUrl;
    }
    public Long getCounterStartMills() {
        return counter.getCalStartMills();
    }
    public Long getCounterEndMills() {
        return counter.getCalEndMills();
    }

}