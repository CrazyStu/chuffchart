package com.totirrapp.cc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class DetailsActivityCountUp extends Activity {
    private boolean running = true;
    private detailsThread MT;
    private String chartStart = "02/02/2002";
    private String chartEnd = "02/02/2020";
    private CounterFragment counter;
    private ArrayList<String> values;
    private ProgressBar totalProgress;
    private ProgressBar monthProgress;
    private ProgressBar weekProgress;
    private ProgressBar dayProgress;
    private ProgressBar hourProgress;
    private ProgressBar minuteProgress;
    private ProgressBar secondProgress;

    private TextView targetDate;
    private TextView percent;
    private TextView monthCount;
    private TextView weekCount;
    private TextView dayCount;
    private TextView hourCount;
    private TextView minuteCount;
    private TextView secondCount;



    public DetailsActivityCountUp() {
//        counter = new CounterFragment();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            chartStart = getIntent().getExtras().getString("start");
            chartEnd = getIntent().getExtras().getString("end");
            Log.e("gotExtras?", "start#end " + chartStart + "#" + chartEnd);

        } catch (Exception e) {
            e.printStackTrace();
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.details_activity_count_up);
        setupView();

    }

    protected void onPause() {
        super.onPause();
        running = false;
        Log.e("DetailsTread State", MT.getState() + "");
    }

    protected void onResume() {
        super.onResume();
        running = true;
        try {
            MT = null;
            MT = new detailsThread();
            MT.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setupView() {
        counter = new CounterFragment(chartStart, chartEnd);
        targetDate = (TextView) findViewById(R.id.details_activity_target_date);
        percent = (TextView) findViewById(R.id.details_activity_percentage);
        monthCount = (TextView) findViewById(R.id.details_activity_months_count);
        weekCount = (TextView) findViewById(R.id.details_activity_weeks_count);
        dayCount = (TextView) findViewById(R.id.details_activity_days_count);
        hourCount = (TextView) findViewById(R.id.details_activity_hours_count);
        minuteCount = (TextView) findViewById(R.id.details_activity_minutes_count);
        secondCount = (TextView) findViewById(R.id.details_activity_seconds_count);

        totalProgress = (ProgressBar) findViewById(R.id.details_activity_progress_total);
        monthProgress = (ProgressBar) findViewById(R.id.details_activity_progress_months);
        weekProgress = (ProgressBar) findViewById(R.id.details_activity_progress_weeks);
        dayProgress = (ProgressBar) findViewById(R.id.details_activity_progress_days);
        hourProgress = (ProgressBar) findViewById(R.id.details_activity_progress_hours);
        minuteProgress = (ProgressBar) findViewById(R.id.details_activity_progress_minutes);
        secondProgress = (ProgressBar) findViewById(R.id.details_activity_progress_seconds);
    }

    public void updateDetailsView() {
        Log.i("detailsFrag", "updateDetailsView()");
        targetDate.setText(chartEnd);
        percent.setText(counter.getPercentDone() + "%");
        monthCount.setText(Integer.toString(counter.getMonthsDone()));
        weekCount.setText(Integer.toString(counter.getWeeksDone()));
        dayCount.setText(Integer.toString(counter.getDaysDone()));
        hourCount.setText(Integer.toString(counter.getHoursDone()));
        minuteCount.setText(Integer.toString(counter.getMinsDone()));
        secondCount.setText(Integer.toString(counter.getSecsDone()));
    }
    private void checkVisibility(){
        if(counter.getMonthsDone()<1){
            monthProgress.setVisibility(View.GONE);
            findViewById(R.id.details_activity_months_view).setVisibility(View.GONE);
        }else{
            monthProgress.setVisibility(View.VISIBLE);
            findViewById(R.id.details_activity_months_view).setVisibility(View.VISIBLE);
        }
        if(counter.getWeeksDone()<1){
            weekProgress.setVisibility(View.GONE);
            findViewById(R.id.details_activity_weeks_view).setVisibility(View.GONE);
        }else{
            weekProgress.setVisibility(View.VISIBLE);
            findViewById(R.id.details_activity_weeks_view).setVisibility(View.VISIBLE);
        }
        if(counter.getDaysDone()<1){
            dayProgress.setVisibility(View.GONE);
            findViewById(R.id.details_activity_days_view).setVisibility(View.GONE);
        }else{
            dayProgress.setVisibility(View.VISIBLE);
            findViewById(R.id.details_activity_days_view).setVisibility(View.VISIBLE);
        }
    }
    private class detailsThread extends Thread {
        public void run() {
            Log.e("MT State", MT.getState() + "");
            while (running) {
                try {
                    counter.updateCounter();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            updateDetailsView();
                            checkVisibility();
                        }
                    });
                    totalProgress.setProgress(counter.getPercentDone());
                    monthProgress.setProgress((int)((float)counter.getMonthsDone()/12*100));
                    weekProgress.setProgress((int)((float)counter.getWeeksDone()/4*100));
                    dayProgress.setProgress((int)((float)counter.getDaysDone()/7*100));
                    hourProgress.setProgress((int) ((float)counter.getHoursDone() / 24*100));
                    minuteProgress.setProgress((int) ((float)counter.getMinsDone()/60*100));
                    secondProgress.setProgress((int) ((float)counter.getSecsDone()/60*100));
                    sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.e("MT State", MT.getState() + "stopped");
        }
    }

}