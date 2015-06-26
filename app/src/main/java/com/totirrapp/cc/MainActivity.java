package com.totirrapp.cc;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
		implements NavigationDrawerFragment.NavigationDrawerCallbacks,ChartFragment.clickCallback, NewFragment.newChartCallback{
	public static Context			context;
	private static int				RESULT_LOAD_IMAGE	= 1;
	private String					noImage				= "noImage";
	private boolean					running				= false;
	private detailsThread			MT;
	private Bitmap bg1;
	private int screenHMem;
	private int screenWMem;
	private String newStartDate;
	private String newEndDate;
	private static String[] test;
//	private FragmentManager 		fm = this.getSupportFragmentManager();
	private ArrayList<ChartFragment> chartFragList;
	private int width;
	private int height;
	private int currentPageNo;
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private CharSequence mTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		context = this.getBaseContext();
		new databaseReader("Activity Create");
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;
		initCharts();
		setContentView(R.layout.activity_main);
		mNavigationDrawerFragment=(NavigationDrawerFragment)
				getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle=getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(
				R.id.navigation_drawer,
				(DrawerLayout)findViewById(R.id.drawer_layout));
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
	public void initCharts(){
			chartFragList = new ArrayList<ChartFragment>();
			databaseReader.getChartCount("initCharts()");
			int x=0;
			ChartFragment chartFrag;
			while(x<DBV.chartCount){
				Log.e("#--Create Chart--# ", "Making chart no#"+x);
				chartFrag=new ChartFragment();
				chartFragList.add(x, chartFrag);
				Bundle args=new Bundle();
				args.putInt("ChartNo", x);
				args.putInt("Height", height);
				args.putInt("Width", width);
//				args.putInt("secNo", x+1);
				chartFrag.setArguments(args);
				chartFrag.getArgs();
				x++;
			}
		initListItems();
	}
	public void initListItems(){
		int y =chartFragList.size();
		test = new String[y+2];
		int x = 0;
		while(x<y){
			test[x]=chartFragList.get(x).getChartName();
			x++;
		};
		test[x++]="New";
		test[x]="Help";

	}
	public static String[] getNavMenuList(){
		return test;
	}
	@Override
	public void onNavigationDrawerItemSelected(int position){
		Fragment fragment;
		currentPageNo = position;
		if (position <= DBV.chartCount-1) {
			fragment= chartFragList.get(position);
			mTitle=getPageTitle();
		}else if(position == DBV.chartCount) {
			fragment= new NewFragment();
			mTitle="New Chart?";
		} else {
			fragment = new HelpFragment();
			mTitle="Help";
		}
		FragmentManager fragmentManager=getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.container, fragment)
				.commit();
	}

	public void restoreActionBar(){
		ActionBar actionBar=getSupportActionBar();
//		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
//		actionBar.hide();
		actionBar.setTitle(mTitle);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		if(!mNavigationDrawerFragment.isDrawerOpen()){
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id=item.getItemId();
		
		//noinspection SimplifiableIfStatement
		if(id==R.id.action_settings){
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	public Bitmap getBackground(String url,int chartNo, int screenW,int screenH){
		// Context context = MainActivity.context;
		Log.i("BgHandler", "Start Bitmap Processing");
		if (url.equals(noImage)) {
			Log.i("BgHandler", "No BG set Loading default background");
			useWallpaper(chartNo);
			bg1=null;
		} else {
			BitmapFactory.Options bgOptions = new BitmapFactory.Options();
			Bitmap test;
			bgOptions.inJustDecodeBounds = true;
			try {
				test = BitmapFactory.decodeFile(url, bgOptions);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (bgOptions.outHeight > 0) {
				Log.i("BgHandler", "Image found---Loading selected picture");
				bg1 = loadSelectedImage(url, screenW, screenH);
			} else {
				Log.i("BgHandler", "Image not found---Loading default background");
				bg1=null;
			}
		}
		return bg1;
	}
	private Bitmap loadSelectedImage(String bgURL,int screenW,int screenH){
		if(screenHMem==0){
			screenHMem = screenH;
			screenWMem = screenW;
		}
		boolean landscape = false;
		BitmapFactory.Options bgOptions = new BitmapFactory.Options();
		// ========Check size and get sample size======================
		bgOptions.inJustDecodeBounds = true;
		bg1 = BitmapFactory.decodeFile(bgURL, bgOptions);
		int imageHeight = bgOptions.outHeight;
		int imageWidth = bgOptions.outWidth;
		float ScreenAspect = (float) screenHMem / (float) screenWMem;
		float ImageAspect = (float) bgOptions.outHeight / (float) bgOptions.outWidth;
		if (ImageAspect < 1) {
			landscape = true;
		}
		Log.i("Screen","W="+screenWMem+" H= " + screenHMem+"Landscape=" + landscape);
		Log.i("Image","W="+bgOptions.outWidth+" H= " + bgOptions.outHeight+"Landscape=" + landscape);
		int iscale;
		if (landscape) {
			ImageAspect = (float) bgOptions.outWidth / (float) bgOptions.outHeight;
			if (imageHeight > screenHMem) {
				iscale = (int) ((float) imageHeight / (float) screenHMem);
				bgOptions.inSampleSize = iscale;
			} else {
				iscale = 1;
				bgOptions.inSampleSize = iscale;
			}
		} else {
			ImageAspect = (float) bgOptions.outHeight / (float) bgOptions.outWidth;
			if (imageWidth > screenWMem) {
				iscale = (int) ((float) imageWidth / (float) screenWMem) + 2;
				bgOptions.inSampleSize = iscale;
			} else {
				iscale = 1;
				bgOptions.inSampleSize = iscale;
			}
		}

		Log.i("Image","SampleSize="+iscale);
		// =============Read rotation==============================
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(bgURL);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int orientation = 0;
		try{
			orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
		}catch (NullPointerException e){
			e.printStackTrace();
		}
		// =============Get full image and resize=====================
		bgOptions.inJustDecodeBounds = false;
		bg1 = BitmapFactory.decodeFile(bgURL, bgOptions);
		Log.i("Image", "Width="+bg1.getWidth()+"Height= " + bg1.getHeight());
		if (landscape) {
			bg1 = Bitmap.createScaledBitmap(bg1, (int) (screenHMem * ImageAspect), screenHMem, true);
		} else {
			bg1 = Bitmap.createScaledBitmap(bg1, screenWMem, (int) (screenWMem * ImageAspect), true);
//			Log.i("BgHandler", "Size=" + screenW + " x " + (int) (screenW * ImageAspect));
		}
		// ==============rotate as required============================
		int degrees = 0;
		if (orientation == 3) {
			degrees = 180;
		} else if (orientation == 6) {
			degrees = 90;
		} else if (orientation == 8) {
			degrees = 270;
		}
		Matrix matrix = new Matrix();
		matrix.postRotate(degrees);
		bg1 = Bitmap.createBitmap(bg1, 0, 0, bg1.getWidth(), bg1.getHeight(), matrix, true);
		return bg1;
	}
	private void useWallpaper(int chartNo){
		chartFragList.get(currentPageNo).removeBackground();
	}
	public void updateHomeView(){
		Log.e(">>UpdateChart<<", "FragID="+currentPageNo+"/Background="+chartFragList.get(currentPageNo).getChartBgUrl());
		try{
			chartFragList.get(currentPageNo).updateChartView();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	public void onShortPress(int v){
		if (v == R.id.frag_home_parent_view) {
			ChartFragment temp = chartFragList.get(currentPageNo);

//			Intent intent = new Intent(this, DetailsActivityCountDown.class);
			Intent intent = new Intent(this, DetailsTabsActivity.class);
			intent.putExtra("number",currentPageNo);
			intent.putExtra("start", temp.getChartStart());
			intent.putExtra("end",temp.getChartEnd());
			startActivity(intent);
		}
	}
	public void onLongPress(int v){
		if (v == R.id.chart_title) {
			myTitleDialog();
		} else if (v == R.id.timeDoneTextView) {
			myDateDialog(true);
		} else if (v == R.id.timeLeftTextView) {
			myDateDialog(false);
		} else if (v == R.id.frag_home_parent_view) {
			myBGDialog();
		}
	}
	public void initiateBG(int x){
		Log.e("#---initiateBG()---#", "called by chartFrag no#"+x);
		ImageThread imageThread = new ImageThread();
		imageThread.run();
	}
	public void newChartRequest(int v){
		final Dialog newChartDialog = new Dialog(this);
		newChartDialog.setContentView(R.layout.dialog_new);
		newChartDialog.setTitle(R.string.newChartText);
		final EditText newTitle = (EditText) newChartDialog.findViewById(R.id.setNewTitle);
		newTitle.setText(R.string.chartName);
		newChartDialog.show();
		TextView submitChart = (TextView) newChartDialog.findViewById(R.id.button2);
		submitChart.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
//				mNavigationDrawerFragment.get
				int x = currentPageNo;
				String t1 = newTitle.getText().toString();
				String t2 = "I can't wait...";
				String t3 = "for what?";
				String t4 = "01/01/2015";
				String t5 = "11/12/2015";
				String t6 = "No Image";
				try {
					databaseReader.newChart(t1, t2, t3, t4, t5, t6);
//					removeCurrentPage();
					recreate();

				} catch (Exception e) {
					e.printStackTrace();
				}
				newChartDialog.dismiss();
			}
		});
	}
	private String getPageTitle(){
		return chartFragList.get(currentPageNo).getChartName();
	}
	private void myDateDialog(final boolean start){
		int x = currentPageNo;
		//remove this/\
		final ChartFragment tempChart = chartFragList.get(x);
		final Dialog setDateDialog = new Dialog(this);
		setDateDialog.setContentView(R.layout.dialog_dates);
		newStartDate = null;
		newEndDate = null;
		View.OnClickListener doneListener;
		View.OnClickListener cancelListener;
		CalendarView.OnDateChangeListener datePicked;
		final Long startDateMills =tempChart.getCounterStartMills();
		final Long endDateMills =tempChart.getCounterEndMills();
		final CalendarView picker1 = (CalendarView) setDateDialog.findViewById(R.id.dialog_dates_picker_1);
		TextView doneButton = (TextView) setDateDialog.findViewById(R.id.dialog_dates_done_button);
		TextView cancelButton = (TextView) setDateDialog.findViewById(R.id.dialog_dates_cancel_button);
		cancelListener = new View.OnClickListener() {
			public void onClick(View view){
				setDateDialog.dismiss();
			}
		};

		if(start) {
			setDateDialog.setTitle("Set Start Date");
			datePicked = new CalendarView.OnDateChangeListener() {
				@Override
				public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
					Log.i("View selected", "Picker>" + view);
					newStartDate = dayOfMonth + "/" + (month + 1) + "/" + year;
					if(picker1.getDate()>endDateMills){
						newEndDate =  (dayOfMonth+1) + "/" + (month + 1) + "/" + year;
					};
				}
			};
			doneListener = new View.OnClickListener() {
				public void onClick(View view){
					setDateDialog.dismiss();
					if(newEndDate!=null){
						databaseReader.updateBothDate(tempChart.getChartName(), newStartDate, newEndDate);
					}else{
						databaseReader.updateStartDate(tempChart.getChartName(), newStartDate);
					}
					tempChart.readChartValues();
				}
			};
			picker1.setDate(startDateMills, true, true);
		}else {
			setDateDialog.setTitle("Set End Date");
			datePicked = new CalendarView.OnDateChangeListener() {
				@Override
				public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
					Log.i("View selected", "Picker>" + view);
					newEndDate = dayOfMonth + "/" + (month + 1) + "/" + year;
					if(picker1.getDate()<startDateMills){
						newStartDate =  (dayOfMonth-1) + "/" + (month + 1) + "/" + year;
					};
				}
			};
			doneListener = new View.OnClickListener() {
				public void onClick(View view){
					setDateDialog.dismiss();
					if(newStartDate!=null){
						databaseReader.updateBothDate(tempChart.getChartName(), newStartDate, newEndDate);
					}else{
						databaseReader.updateEndDate(tempChart.getChartName(), newEndDate);
					}
					tempChart.readChartValues();
				}
			};
			picker1.setDate(endDateMills, true, true);
		}
		picker1.setOnDateChangeListener(datePicked);
		doneButton.setOnClickListener(doneListener);
		cancelButton.setOnClickListener(cancelListener);
		setDateDialog.show();
	}
	private void myTitleDialog(){
		final Dialog setTitle = new Dialog(this);
		setTitle.setContentView(R.layout.dialog_title);
		setTitle.setTitle("Set Title Text");
		final EditText newTitle = (EditText) setTitle.findViewById(R.id.setNewTitle);
		int x = currentPageNo;
		//\\ remove this
		newTitle.setText(chartFragList.get(x).getChartTitle());
		setTitle.show();
		newTitle.requestFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(newTitle, InputMethodManager.SHOW_FORCED);
		Button submitTitle = (Button) setTitle.findViewById(R.id.button2);
		submitTitle.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
//				int y = mViewPager.getCurrentItem();
				databaseReader.updateTitle(chartFragList.get(currentPageNo).getChartName(), newTitle.getText().toString());
				chartFragList.get(currentPageNo).readChartValues();
				setTitle.dismiss();
			}
		});
		newTitle.requestFocus();
	}

	private void myBGDialog(){
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_background);
		dialog.setTitle("Set background Image");

		dialog.show();
		TextView wallpaperButton = (TextView) dialog.findViewById(R.id.wallpaperButton);
		wallpaperButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				dialog.dismiss();
				databaseReader.updateBGURL(getPageTitle(), noImage);
				chartFragList.get(currentPageNo).readChartValues();
				useWallpaper(currentPageNo);
			}
		});
		TextView galleryButton = (TextView) dialog.findViewById(R.id.galleryButton);
		galleryButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view){
				dialog.dismiss();
				choosePic();
			}
		});
		TextView deleteButton = (TextView) dialog.findViewById(R.id.delete_button);
		deleteButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				dialog.dismiss();
				removeCurrentPage();
			}
		});
	}
	// ## Choose Picture / Update DB / Scale Image / Set Background
	public void choosePic(){
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, RESULT_LOAD_IMAGE);
	}
	protected void onActivityResult(int requestCode,int resultCode,Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Thumbnails.DATA };
			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			databaseReader.updateBGURL(getPageTitle(),picturePath);
			chartFragList.get(currentPageNo).readChartValues();//
			ImageThread imageThread = new ImageThread();
			imageThread.run();
		}
	}
	public void removeCurrentPage(){
		databaseReader.deleteChart(getPageTitle());
		recreate();
	}

	private class detailsThread extends Thread {
		public void run(){
			Log.e("MT State", MT.getState()+"");
			while (running) {
				if(currentPageNo<=DBV.chartCount-1) {
					try {
//						SetCounter.updateCounter();
						runOnUiThread(new Runnable() {
							public void run() {
								updateHomeView();
							}
						});
						sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			Log.e("MT State", MT.getState() +"stopped");
		}
	}
	private class ImageThread extends Thread {
		public void run(){
			Log.e("#---ImageThread---#","Update BG on Chart#"+currentPageNo);
			try {
				int x =currentPageNo;
				String url = chartFragList.get(x).getChartBgUrl();
				BitmapDrawable bgImage = new BitmapDrawable(context.getResources(), getBackground(url,x,width, height));
				bgImage.setGravity(Gravity.CENTER);
				if(bgImage==null){
					useWallpaper(x);
				}else{
					chartFragList.get(x).setNewBackground(bgImage);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.e("imageThread", "Thread ended");
		}
	}
}
