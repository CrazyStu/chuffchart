package com.totirrapp.cc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DetailsTabsActivity extends AppCompatActivity{
	private boolean running = true;
	private detailsThread MT;
	private String chartStart = "02/02/2002";
	private String chartEnd = "02/02/2020";
	private CounterFragment counter;
//	private ArrayList<String> values;
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

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setTheme(R.style.AppTheme);
		setContentView(R.layout.activity_detail_tabs);
		mSectionsPagerAdapter=new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager=(ViewPager)findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		try {
			chartStart = getIntent().getExtras().getString("start");
			chartEnd = getIntent().getExtras().getString("end");
			Log.e("gotExtras?", "start#end " + chartStart + "#" + chartEnd);
			counter = new CounterFragment(chartStart, chartEnd);

		} catch (Exception e) {
			e.printStackTrace();
		}

		setupView();
	}
	protected void onPause(){
		super.onPause();
		running = false;
		Log.e("MT State", MT.getState() + "");
	}
	protected void onResume(){
		super.onResume();
		running = true;
		// Log.e("MT State", MT.getState()+"");
		try {
			MT = null;
			MT = new detailsThread();
			MT.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void setupView() {
		Log.i("setupView()", "Begin Setup with start/end"+chartStart+"/"+chartEnd);
//		counter = new CounterFragment(chartStart, chartEnd);
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
		setupView();
		targetDate.setText(chartEnd);
		percent.setText(counter.getPercentLeft() + "%");
		monthCount.setText(Integer.toString(counter.getMonthsLeft()));
		weekCount.setText(Integer.toString(counter.getWeeksLeft()));
		dayCount.setText(Integer.toString(counter.getDaysLeft()));
		hourCount.setText(Integer.toString(counter.getHoursLeft()));
		minuteCount.setText(Integer.toString(counter.getMinsLeft()));
		secondCount.setText(Integer.toString(counter.getSecsLeft()));
		totalProgress.setProgress(counter.getPercentLeft());
					monthProgress.setProgress((int)((float)counter.getMonthsLeft()/12*100));
					weekProgress.setProgress((int)((float)counter.getWeeksLeft()/4*100));
					dayProgress.setProgress((int)((float)counter.getDaysLeft()/7*100));
					hourProgress.setProgress((int) ((float)counter.getHoursLeft() / 24*100));
					minuteProgress.setProgress((int) ((float)counter.getMinsLeft()/60*100));
					secondProgress.setProgress((int) ((float)counter.getSecsLeft()/60*100));
	}
	private void checkVisibility(){
		if(counter.getMonthsLeft()<1){
			monthProgress.setVisibility(View.GONE);
			findViewById(R.id.details_activity_months_view).setVisibility(View.GONE);
		}else{
			monthProgress.setVisibility(View.VISIBLE);
			findViewById(R.id.details_activity_months_view).setVisibility(View.VISIBLE);
		}
		if(counter.getWeeksLeft()<1){
			weekProgress.setVisibility(View.GONE);
			findViewById(R.id.details_activity_weeks_view).setVisibility(View.GONE);
		}else{
			weekProgress.setVisibility(View.VISIBLE);
			findViewById(R.id.details_activity_weeks_view).setVisibility(View.VISIBLE);
		}
		if(counter.getDaysLeft()<1){
			dayProgress.setVisibility(View.GONE);
			findViewById(R.id.details_activity_days_view).setVisibility(View.GONE);
		}else{
			dayProgress.setVisibility(View.VISIBLE);
			findViewById(R.id.details_activity_days_view).setVisibility(View.VISIBLE);
		}
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter{
		
		public SectionsPagerAdapter(FragmentManager fm){
			super(fm);
		}
		
		@Override
		public Fragment getItem(int position){
			if(position==1){
				return new DetailsFragmentCountDown();
			}else{
				return new DetailsFragmentCountUp();
			}
		}
		
		@Override
		public int getCount(){
			return 2;
		}

	}

	private class detailsThread extends Thread {
		public void run() {
			Log.e("MT State", MT.getState()+"!");
			while (running) {
				try {
					counter.updateCounter();
					runOnUiThread(new Runnable() {
						public void run() {
							updateDetailsView();
							checkVisibility();
						}
					});
//					totalProgress.setProgress(counter.getPercentLeft());
//					monthProgress.setProgress((int)((float)counter.getMonthsLeft()/12*100));
//					weekProgress.setProgress((int)((float)counter.getWeeksLeft()/4*100));
//					dayProgress.setProgress((int)((float)counter.getDaysLeft()/7*100));
//					hourProgress.setProgress((int) ((float)counter.getHoursLeft() / 24*100));
//					minuteProgress.setProgress((int) ((float)counter.getMinsLeft()/60*100));
//					secondProgress.setProgress((int) ((float)counter.getSecsLeft()/60*100));
					sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			Log.e("MT State", MT.getState() + "stopped");
		}
	}

}
