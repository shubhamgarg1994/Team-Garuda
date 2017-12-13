package org.gcs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import org.gcs.CameraFeed;
import org.gcs.CameraSettings;
import org.gcs.MjpegInputStream;
import org.gcs.MjpegView;
import org.gcs.R;
import org.gcs.activitys.helpers.SuperFlightActivity;
import org.gcs.drone.DroneInterfaces.DroneTypeListner;
import org.gcs.fragments.FlightMapFragment;
import org.gcs.fragments.HudFragment;
import org.gcs.fragments.RCFragment;
import org.gcs.widgets.viewPager.MapViewPager;

public class CameraRC extends SuperFlightActivity implements
		DroneTypeListner {

	AdapterHudMap mAdapter;
	MapViewPager mPager;
	
	private static final boolean DEBUG=false;
    private static final String TAG = "MJPEG";

    private MjpegView mv = null;
    String URL;
    
    // for settings (network and resolution)
    private static final int REQUEST_SETTINGS = 0;
    
    private int width = 640;
    private int height = 480;
    
    private int ip_ad1 = 192;
    private int ip_ad2 = 168;
    private int ip_ad3 = 1;
    private int ip_ad4 = 102;
    private int ip_port = 8090;
    private String ip_command = "?action=stream";
    private String path = Environment.getDataDirectory().toString();
    
    private boolean suspending = false;
    
	final Handler handler = new Handler();

	@Override
	public int getNavigationItem() {
		return 7;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences preferences = getSharedPreferences("SAVED_VALUES", MODE_PRIVATE);
       // width = preferences.getInt("width", width);
       // height = preferences.getInt("height", height);
        ip_ad1 = preferences.getInt("ip_ad1", ip_ad1);
        ip_ad2 = preferences.getInt("ip_ad2", ip_ad2);
        ip_ad3 = preferences.getInt("ip_ad3", ip_ad3);
        ip_ad4 = preferences.getInt("ip_ad4", ip_ad4);
        ip_port = preferences.getInt("ip_port", ip_port);
        ip_command = preferences.getString("ip_command", ip_command);
        path = preferences.getString("path", path);
                
        StringBuilder sb = new StringBuilder();
        String s_http = "http://";
        String s_dot = ".";
        String s_colon = ":";
        String s_slash = "/";
        sb.append(s_http);
        sb.append(ip_ad1);
        sb.append(s_dot);
        sb.append(ip_ad2);
        sb.append(s_dot);
        sb.append(ip_ad3);
        sb.append(s_dot);
        sb.append(ip_ad4);
        sb.append(s_colon);
        sb.append(ip_port);
        sb.append(s_slash);
        sb.append(ip_command);
        URL = new String(sb);

		setContentView(R.layout.activity_camera_rc);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		mAdapter = new AdapterHudMap(getFragmentManager());
		mPager = (MapViewPager) findViewById(R.id.rcPager);
		if (mPager != null) {
			mPager.setAdapter(mAdapter);
			mPager.setSwipeMarginWidth(40);
		}

		drone.mission.missionListner = this;
		drone.setDroneTypeChangedListner(this);
		
		mv = (MjpegView) findViewById(R.id.mv);  
        //if(mv != null){
        	//mv.setResolution(width, height);
        //}
        
        setTitle(R.string.title_connecting);
        new DoRead().execute(URL);
	}
	
	public void onResume() {
    	if(DEBUG) Log.d(TAG,"onResume()");
        super.onResume();
        if(mv!=null){
        	if(suspending){
        		new DoRead().execute(URL);
        		suspending = false;
        	}
        }

    }

    public void onStart() {
    	if(DEBUG) Log.d(TAG,"onStart()");
        super.onStart();
    }
    public void onPause() {
    	if(DEBUG) Log.d(TAG,"onPause()");
        super.onPause();
        if(mv!=null){
        	if(mv.isStreaming()){
		        mv.stopPlayback();
		        suspending = true;
        	}
        }
    }
    public void onStop() {
    	if(DEBUG) Log.d(TAG,"onStop()");
        super.onStop();
    }

    public void onDestroy() {
    	if(DEBUG) Log.d(TAG,"onDestroy()");
    	
    	//if(mv!=null){
    	//	mv.freeCameraMemory();
    	//}
    	
        super.onDestroy();
    }

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_rc, menu);
		MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.option_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_zoom:
			if (mapFragment != null)
				mapFragment.zoomToLastKnowPosition();
			return true;
		case R.id.menu_clearFlightPath:
			if (mapFragment != null)
				mapFragment.clearFlightPath();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	public class AdapterHudMap extends FragmentPagerAdapter {
		static final int NUM_ADAPTER_FRAGMENT_ITEMS = 2;

		public AdapterHudMap(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return NUM_ADAPTER_FRAGMENT_ITEMS;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				hudFragment = new HudFragment();
				return hudFragment;
			case 1:
				mapFragment = new FlightMapFragment();
				return mapFragment;
			default:
				return null;
			}
		}
	}
	

	//Handle movement of wikipad joystick
	@Override
	public boolean onGenericMotionEvent(MotionEvent ev){
		RCFragment rcFragment = (RCFragment) getFragmentManager().findFragmentById(R.id.rcFragment);
		return rcFragment.physicalJoyMoved(ev);
	}
	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		case R.id.settings:
    			Intent settings_intent = new Intent(CameraRC.this, CameraSettings.class);
    			//settings_intent.putExtra("width", width);
    			//settings_intent.putExtra("height", height);
    			settings_intent.putExtra("ip_ad1", ip_ad1);
    			settings_intent.putExtra("ip_ad2", ip_ad2);
    			settings_intent.putExtra("ip_ad3", ip_ad3);
    			settings_intent.putExtra("ip_ad4", ip_ad4);
    			settings_intent.putExtra("ip_port", ip_port);
    			settings_intent.putExtra("ip_command", ip_command);
    			settings_intent.putExtra("path",path);
    			startActivityForResult(settings_intent, REQUEST_SETTINGS);
    			return true;
    			
    		case R.id.action_capture:

				Log.e("aaa", "Capture button is clicked");
				FileOutputStream out = null;
				{
					Bitmap bm = mv.kvl;
					if (bm == null) {
						Log.e("aaaaa", "bm is null");
					} else {
						Toast.makeText(getApplicationContext(), "Image Captureded", Toast.LENGTH_SHORT).show();
						Log.e("aaaaa", "Else entered");
						Log.e("message:", "Reached try");

						File myDir = new File(path);
						myDir.mkdirs();
						//Random generator = new Random();
						//int n = 10000;
						//n = generator.nextInt(n);
						SimpleDateFormat s=new SimpleDateFormat("ddMMyyyyhhmmss");
						String n1=s.format(new Date());
						String fname = "Image-" + n1 + ".jpg";
						File file = new File(myDir, fname);
						if (file.exists())
							file.delete();
						try {
							FileOutputStream out1 = new FileOutputStream(file);
							bm.compress(Bitmap.CompressFormat.JPEG, 90, out1);
							out1.flush();
							out1.close();

						} catch (Exception e) {
							e.printStackTrace();
						}

    	}
				}}
    	return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch (requestCode) {
    		case REQUEST_SETTINGS:
    			if (resultCode == Activity.RESULT_OK) {
    				//width = data.getIntExtra("width", width);
    				//height = data.getIntExtra("height", height);
    				ip_ad1 = data.getIntExtra("ip_ad1", ip_ad1);
    				ip_ad2 = data.getIntExtra("ip_ad2", ip_ad2);
    				ip_ad3 = data.getIntExtra("ip_ad3", ip_ad3);
    				ip_ad4 = data.getIntExtra("ip_ad4", ip_ad4);
    				ip_port = data.getIntExtra("ip_port", ip_port);
    				ip_command = data.getStringExtra("ip_command");
    				path = data.getStringExtra("path");

    				//if(mv!=null){
    				//	mv.setResolution(width, height);
    				//}
    				SharedPreferences preferences = getSharedPreferences("SAVED_VALUES", MODE_PRIVATE);
    				SharedPreferences.Editor editor = preferences.edit();
    				//editor.putInt("width", width);
    				//editor.putInt("height", height);
    				editor.putInt("ip_ad1", ip_ad1);
    				editor.putInt("ip_ad2", ip_ad2);
    				editor.putInt("ip_ad3", ip_ad3);
    				editor.putInt("ip_ad4", ip_ad4);
    				editor.putInt("ip_port", ip_port);
    				editor.putString("ip_command", ip_command);
    				editor.putString("path",path);

    				editor.commit();

    				new RestartApp().execute();
    			}
    			break;
    	}
    }

    public void setImageError(){
    	handler.post(new Runnable() {
    		@Override
    		public void run() {
    			setTitle(R.string.title_imageerror);
    			return;
    		}
    	});
    }
    
    public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {
        protected MjpegInputStream doInBackground(String... url) {
            //TODO: if camera has authentication deal with it and don't just not work
            HttpResponse res = null;         
            DefaultHttpClient httpclient = new DefaultHttpClient(); 
            HttpParams httpParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 5*1000);
            HttpConnectionParams.setSoTimeout(httpParams, 5*1000);
            if(DEBUG) Log.d(TAG, "1. Sending http request");
            try {
                res = httpclient.execute(new HttpGet(URI.create(url[0])));
                if(DEBUG) Log.d(TAG, "2. Request finished, status = " + res.getStatusLine().getStatusCode());
                if(res.getStatusLine().getStatusCode()==401){
                    //You must turn off camera User Access Control before this will work
                    return null;
                }
                return new MjpegInputStream(res.getEntity().getContent());  
            } catch (ClientProtocolException e) {
            	if(DEBUG){
	                e.printStackTrace();
	                Log.d(TAG, "Request failed-ClientProtocolException", e);
            	}
                //Error connecting to camera
            } catch (IOException e) {
            	if(DEBUG){
	                e.printStackTrace();
	                Log.d(TAG, "Request failed-IOException", e);
            	}
                //Error connecting to camera
            }
            return null;
        }

        protected void onPostExecute(MjpegInputStream result) {
            mv.setSource(result);
            if(result!=null){
            	result.setSkip(1);
            	setTitle(R.string.app_name);
            }else{
            	setTitle(R.string.title_disconnected);
            }
            mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
            mv.showFps(false);
        }
    }
    
    public class RestartApp extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... v) {
        	CameraRC.this.finish();
            return null;
        }

        protected void onPostExecute(Void v) {
        	startActivity((new Intent(CameraRC.this, CameraFeed.class)));
        }
    }
	
	
	
}