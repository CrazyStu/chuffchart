package com.totirrapp.cc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DetailsTabsActivity extends AppCompatActivity{
	private boolean running = true;
	private detailsThread MT;
	private String chartStart = "02/02/2002";
	private String chartEnd = "02/02/2020";
	private CounterFragment counter;
	private ProgressBar totalProgressUp;
	private ProgressBar monthProgressUp;
	private ProgressBar weekProgressUp;
	private ProgressBar dayProgressUp;
	private ProgressBar hourProgressUp;
	private ProgressBar minuteProgressUp;
	private ProgressBar secondProgressUp;

	private TextView targetDateUp;
	private TextView percentUp;
	private TextView monthCountUp;
	private TextView weekCountUp;
	private TextView dayCountUp;
	private TextView hourCountUp;
	private TextView minuteCountUp;
	private TextView secondCountUp;

	private ProgressBar totalProgressDown;
	private ProgressBar monthProgressDown;
	private ProgressBar weekProgressDown;
	private ProgressBar dayProgressDown;
	private ProgressBar hourProgressDown;
	private ProgressBar minuteProgressDown;
	private ProgressBar secondProgressDown;

	private TextView targetDateDown;
	private TextView percentDown;
	private TextView monthCountDown;
	private TextView weekCountDown;
	private TextView dayCountDown;
	private TextView hourCountDown;
	private TextView minuteCountDown;
	private TextView secondCountDown;


	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	public DetailsTabsActivity(){	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
//		setTheme(R.style.AppTheme);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER,WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);
//		setTheme(R.style.AppTheme);
		setContentView(R.layout.activity_detail_tabs);
		mSectionsPagerAdapter=new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager=(ViewPager)findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setPageMargin(-45);
		try {
			chartStart = getIntent().getExtras().getString("start");
			chartEnd = getIntent().getExtras().getString("end");
			Log.e("gotExtras?", "start#end " + chartStart + "#" + chartEnd);
			counter = new CounterFragment(chartStart, chartEnd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ActionBar actionBar=getSupportActionBar();
		actionBar.hide();
//		setupViewCountDown();
	}
	protected void onPause(){
		super.onPause();
		running = false;
		Log.e("MT State", MT.getState() + "");
	}
	protected void onResume(){
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
	public void updateCountUp(){
		try{
			setupViewCountUp();
			updateDetailsViewCountUp();
			checkVisibilityCountUp();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void setupViewCountUp() {
		targetDateUp = (TextView) findViewById(R.id.details_activity_target_date_up);
		percentUp = (TextView) findViewById(R.id.details_activity_percentage_up);
		monthCountUp = (TextView) findViewById(R.id.details_activity_months_count_up);
		weekCountUp = (TextView) findViewById(R.id.details_activity_weeks_count_up);
		dayCountUp = (TextView) findViewById(R.id.details_activity_days_count_up);
		hourCountUp = (TextView) findViewById(R.id.details_activity_hours_count_up);
		minuteCountUp = (TextView) findViewById(R.id.details_activity_minutes_count_up);
		secondCountUp = (TextView) findViewById(R.id.details_activity_seconds_count_up);

		totalProgressUp = (ProgressBar) findViewById(R.id.details_activity_progress_total_up);
		monthProgressUp = (ProgressBar) findViewById(R.id.details_activity_progress_months_up);
		weekProgressUp = (ProgressBar) findViewById(R.id.details_activity_progress_weeks_up);
		dayProgressUp = (ProgressBar) findViewById(R.id.details_activity_progress_days_up);
		hourProgressUp = (ProgressBar) findViewById(R.id.details_activity_progress_hours_up);
		minuteProgressUp = (ProgressBar) findViewById(R.id.details_activity_progress_minutes_up);
		secondProgressUp = (ProgressBar) findViewById(R.id.details_activity_progress_seconds_up);
	}
	public void updateDetailsViewCountUp() {
//		Log.i("detailsFrag", "updateDetailsView()");
		targetDateUp.setText(chartStart);
		percentUp.setText(counter.getPercentDone() + "%");
		monthCountUp.setText(Integer.toString(counter.getMonthsDone()));
		weekCountUp.setText(Integer.toString(counter.getWeeksDone()));
		dayCountUp.setText(Integer.toString(counter.getDaysDone()));
		hourCountUp.setText(Integer.toString(counter.getHoursDone()));
		minuteCountUp.setText(Integer.toString(counter.getMinsDone()));
		secondCountUp.setText(Integer.toString(counter.getSecsDone()));
		totalProgressUp.setProgress(counter.getPercentDone());
		monthProgressUp.setProgress((int)((float)counter.getMonthsDone()/12*100));
		weekProgressUp.setProgress((int)((float)counter.getWeeksDone()/4*100));
		dayProgressUp.setProgress((int)((float)counter.getDaysDone()/7*100));
		hourProgressUp.setProgress((int) ((float)counter.getHoursDone() / 24*100));
		minuteProgressUp.setProgress((int) ((float)counter.getMinsDone()/60*100));
		secondProgressUp.setProgress((int) ((float)counter.getSecsDone()/60*100));
	}
	private void checkVisibilityCountUp(){
		if(counter.getMonthsDone()<1){
			monthProgressUp.setVisibility(View.GONE);
			findViewById(R.id.details_activity_months_view_up).setVisibility(View.GONE);
		}else{
			monthProgressUp.setVisibility(View.VISIBLE);
			findViewById(R.id.details_activity_months_view_up).setVisibility(View.VISIBLE);
		}
		if(counter.getWeeksDone()<1){
			weekProgressUp.setVisibility(View.GONE);
			findViewById(R.id.details_activity_weeks_view_up).setVisibility(View.GONE);
		}else{
			weekProgressUp.setVisibility(View.VISIBLE);
			findViewById(R.id.details_activity_weeks_view_up).setVisibility(View.VISIBLE);
		}
		if(counter.getDaysDone()<1){
			dayProgressUp.setVisibility(View.GONE);
			findViewById(R.id.details_activity_days_view_up).setVisibility(View.GONE);
		}else{
			dayProgressUp.setVisibility(View.VISIBLE);
			findViewById(R.id.details_activity_days_view_up).setVisibility(View.VISIBLE);
		}
	}


	public void updateCountDown(){
		try{
			setupViewCountDown();
			updateDetailsViewCountDown();
			checkVisibilityCountDown();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void setupViewCountDown() {
		targetDateDown = (TextView) findViewById(R.id.details_activity_target_date_down);
		percentDown = (TextView) findViewById(R.id.details_activity_percentage_down);
		monthCountDown = (TextView) findViewById(R.id.details_activity_months_count_down);
		weekCountDown = (TextView) findViewById(R.id.details_activity_weeks_count_down);
		dayCountDown = (TextView) findViewById(R.id.details_activity_days_count_down);
		hourCountDown = (TextView) findViewById(R.id.details_activity_hours_count_down);
		minuteCountDown = (TextView) findViewById(R.id.details_activity_minutes_count_down);
		secondCountDown = (TextView) findViewById(R.id.details_activity_seconds_count_down);

		totalProgressDown = (ProgressBar) findViewById(R.id.details_activity_progress_total_down);
		monthProgressDown = (ProgressBar) findViewById(R.id.details_activity_progress_months_down);
		weekProgressDown = (ProgressBar) findViewById(R.id.details_activity_progress_weeks_down);
		dayProgressDown = (ProgressBar) findViewById(R.id.details_activity_progress_days_down);
		hourProgressDown = (ProgressBar) findViewById(R.id.details_activity_progress_hours_down);
		minuteProgressDown = (ProgressBar) findViewById(R.id.details_activity_progress_minutes_down);
		secondProgressDown = (ProgressBar) findViewById(R.id.details_activity_progress_seconds_down);
	}
	public void updateDetailsViewCountDown() {
//		Log.i("detailsFrag", "updateDetailsView()");
		targetDateDown.setText(chartEnd);
		percentDown.setText(counter.getPercentLeft() + "%");
		monthCountDown.setText(Integer.toString(counter.getMonthsLeft()));
		weekCountDown.setText(Integer.toString(counter.getWeeksLeft()));
		dayCountDown.setText(Integer.toString(counter.getDaysLeft()));
		hourCountDown.setText(Integer.toString(counter.getHoursLeft()));
		minuteCountDown.setText(Integer.toString(counter.getMinsLeft()));
		secondCountDown.setText(Integer.toString(counter.getSecsLeft()));
		totalProgressDown.setProgress(counter.getPercentDone());
		monthProgressDown.setProgress((int)((float)counter.getMonthsLeft()/12*100));
		weekProgressDown.setProgress((int)((float)counter.getWeeksLeft()/4*100));
		dayProgressDown.setProgress((int)((float)counter.getDaysLeft()/7*100));
		hourProgressDown.setProgress((int) ((float)counter.getHoursLeft() / 24*100));
		minuteProgressDown.setProgress((int) ((float)counter.getMinsLeft()/60*100));
		secondProgressDown.setProgress((int) ((float)counter.getSecsLeft()/60*100));
	}
	private void checkVisibilityCountDown(){
		if(counter.getMonthsLeft()<1){
			monthProgressDown.setVisibility(View.GONE);
			findViewById(R.id.details_activity_months_view_down).setVisibility(View.GONE);
		}else{
			monthProgressDown.setVisibility(View.VISIBLE);
			findViewById(R.id.details_activity_months_view_down).setVisibility(View.VISIBLE);
		}
		if(counter.getWeeksLeft()<1){
			weekProgressDown.setVisibility(View.GONE);
			findViewById(R.id.details_activity_weeks_view_down).setVisibility(View.GONE);
		}else{
			weekProgressDown.setVisibility(View.VISIBLE);
			findViewById(R.id.details_activity_weeks_view_down).setVisibility(View.VISIBLE);
		}
		if(counter.getDaysLeft()<1){
			dayProgressDown.setVisibility(View.GONE);
			findViewById(R.id.details_activity_days_view_down).setVisibility(View.GONE);
		}else{
			dayProgressDown.setVisibility(View.VISIBLE);
			findViewById(R.id.details_activity_days_view_down).setVisibility(View.VISIBLE);
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
							if(mViewPager.getCurrentItem()==0){
								updateCountUp();
							}else{
								updateCountDown();
							}

						}
					});
					sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			Log.e("MT State", MT.getState() + "stopped");
		}
	}

}
