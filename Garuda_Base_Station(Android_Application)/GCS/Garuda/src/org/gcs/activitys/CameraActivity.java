package org.gcs.activitys;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;



//import com.camera.simplemjpeg.MjpegActivity;
//import com.camera.simplemjpeg.MjpegInputStream;
//import com.camera.simplemjpeg.MjpegView;


import org.gcs.CameraSettings;
import org.gcs.MjpegInputStream;
import org.gcs.MjpegView;
//import com.camera.simplemjpeg.SettingsActivity;
//import com.camera.simplemjpeg.MjpegActivity.DoRead;
//import com.camera.simplemjpeg.MjpegActivity.RestartApp;
import org.gcs.R;
import org.gcs.activitys.helpers.SuperActivity;
import org.gcs.helpers.RcOutput;
import org.gcs.widgets.joystick.JoystickMovedListener;
import org.gcs.widgets.joystick.JoystickView;

public class CameraActivity extends SuperActivity {

	private MenuItem bTogleCamera;
	private RcOutput rcOutput;
	private TextView textViewLPan, textViewLTilt, textViewRPan, textViewRTilt;
	
	
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
    private int ip_ad3 = 2;
    private int ip_ad4 = 1;
    private int ip_port = 80;
    private String ip_command = "?action=stream";
    
    private boolean suspending = false;
    
	final Handler handler = new Handler();

	@Override
	public int getNavigationItem() {
		return 4;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences preferences = getSharedPreferences("SAVED_VALUES", MODE_PRIVATE);
        //width = preferences.getInt("width", width);
        //height = preferences.getInt("height", height);
        ip_ad1 = preferences.getInt("ip_ad1", ip_ad1);
        ip_ad2 = preferences.getInt("ip_ad2", ip_ad2);
        ip_ad3 = preferences.getInt("ip_ad3", ip_ad3);
        ip_ad4 = preferences.getInt("ip_ad4", ip_ad4);
        ip_port = preferences.getInt("ip_port", ip_port);
        ip_command = preferences.getString("ip_command", ip_command);
                
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

        //setContentView(R.layout.main);
        setContentView(R.layout.activity_camera);
        mv = (MjpegView) findViewById(R.id.mv);  
        //if(mv != null){
        	//mv.setResolution(width, height);
        //}
        
        setTitle(R.string.title_connecting);
        new DoRead().execute(URL);

		

		textViewLPan = (TextView) findViewById(R.id.textViewCamJoyLPan);
		textViewLPan.setText(" RC8: 0%");
		textViewLTilt = (TextView) findViewById(R.id.textViewCamJoyLTilt);
		textViewLTilt.setText("");
		textViewRPan = (TextView) findViewById(R.id.textViewCamJoyRPan);
		textViewRPan.setText("RC7: 0%");
		textViewRTilt = (TextView) findViewById(R.id.textViewCamJoyRTilt);
		textViewRTilt.setText("RC6: 0%");

		JoystickView joystickL = (JoystickView) findViewById(R.id.joystickViewCamL);
		JoystickView joystickR = (JoystickView) findViewById(R.id.joystickViewCamR);

		joystickL.setOnJostickMovedListener(lJoystick);
		joystickR.setOnJostickMovedListener(rJoystick);

		rcOutput = new RcOutput(drone, this);
	}

	@Override
	protected void onDestroy() {
		
if(DEBUG) Log.d(TAG,"onDestroy()");
    	
    	//if(mv!=null){
    		//mv.freeCameraMemory();
    	//}
    	
        //super.onDestroy();
		
		disableRcOverride();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		
		if(DEBUG) Log.d(TAG,"onPause()");
        //super.onPause();
        if(mv!=null){
        	if(mv.isStreaming()){
		        mv.stopPlayback();
		        suspending = true;
        	}
        }
		
		disableRcOverride();
		super.onPause();
	}

	@Override
	protected void onResume() {
		
		if(DEBUG) Log.d(TAG,"onResume()");
        //super.onResume();
        if(mv!=null){
        	if(suspending){
        		new DoRead().execute(URL);
        		suspending = false;
        	}
        }
		
		disableRcOverride();
		super.onResume();
	}
	
	@Override
	public void onStart() {
    	if(DEBUG) Log.d(TAG,"onStart()");
        super.onStart();
    }
	
	@Override
	public void onStop() {
    	if(DEBUG) Log.d(TAG,"onStop()");
        super.onStop();
    }

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_camera, menu);
		bTogleCamera = menu.findItem(R.id.menu_camera_enable);
		
		MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.option_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_camera_enable:
			toggleCameraOverride();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	private void toggleCameraOverride() {
		if (rcOutput.isRcOverrided()) {
			disableRcOverride();
		} else {
			enableRcOverride();
		}
	}

	private void enableRcOverride() {
		rcOutput.enableRcOverride();
		lJoystick.OnMoved(0f, 0f);
		rJoystick.OnMoved(0f, 0f);
		bTogleCamera.setTitle(R.string.disable);
	}

	private void disableRcOverride() {
		rcOutput.disableRcOverride();
		if (bTogleCamera != null) {
			bTogleCamera.setTitle(R.string.enable);
		}
	}
	
	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		case R.id.settings:
    			Intent settings_intent = new Intent(CameraActivity.this, CameraSettings.class);
    			settings_intent.putExtra("width", width);
    			settings_intent.putExtra("height", height);
    			settings_intent.putExtra("ip_ad1", ip_ad1);
    			settings_intent.putExtra("ip_ad2", ip_ad2);
    			settings_intent.putExtra("ip_ad3", ip_ad3);
    			settings_intent.putExtra("ip_ad4", ip_ad4);
    			settings_intent.putExtra("ip_port", ip_port);
    			settings_intent.putExtra("ip_command", ip_command);
    			startActivityForResult(settings_intent, REQUEST_SETTINGS);
    			return true;
    	}
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

    				//if(mv!=null){
    					//mv.setResolution(width, height);
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
        	CameraActivity.this.finish();
            return null;
        }

        protected void onPostExecute(Void v) {
        	startActivity((new Intent(CameraActivity.this, CameraActivity.class)));
        }
    }
	
	
	
	
	
	

	JoystickMovedListener lJoystick = new JoystickMovedListener() {
		@Override
		public void OnReturnedToCenter() {
		}

		@Override
		public void OnReleased() {
		}

		@Override
		public void OnMoved(double pan, double tilt) {
			rcOutput.setRcChannel(RcOutput.RC8, pan);
			textViewLPan.setText(String.format("RC8: %.0f%%", pan * 100));
		}
	};
	JoystickMovedListener rJoystick = new JoystickMovedListener() {
		@Override
		public void OnReturnedToCenter() {
		}

		@Override
		public void OnReleased() {
		}

		@Override
		public void OnMoved(double pan, double tilt) {
			rcOutput.setRcChannel(RcOutput.RC6, tilt);
			rcOutput.setRcChannel(RcOutput.RC7, pan);
			textViewRPan.setText(String.format("RC7: %.0f%%", pan * 100));
			textViewRTilt.setText(String.format("RC6: %.0f%%", tilt * 100));
		}
	};
}