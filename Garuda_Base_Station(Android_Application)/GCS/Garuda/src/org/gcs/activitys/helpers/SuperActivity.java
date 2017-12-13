package org.gcs.activitys.helpers;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import org.gcs.CameraFeed;
import org.gcs.CameraRC;
import org.gcs.CameraSettings;
import org.gcs.DroidPlannerApp;
import org.gcs.Map;
import org.gcs.R;
import org.gcs.DroidPlannerApp.ConnectionStateListner;
import org.gcs.DroidPlannerApp.OnSystemArmListener;
import org.gcs.activitys.CameraActivity;
import org.gcs.activitys.ChartActivity;
import org.gcs.activitys.FlightDataActivity;
import org.gcs.activitys.GCPActivity;
import org.gcs.activitys.ParametersActivity;
import org.gcs.activitys.PlanningActivity;
import org.gcs.activitys.RCActivity;
import org.gcs.activitys.SettingsActivity;
import org.gcs.dialogs.AltitudeDialog;
import org.gcs.dialogs.AltitudeDialog.OnAltitudeChangedListner;
import org.gcs.drone.Drone;
import org.gcs.fragments.helpers.OfflineMapFragment;

import com.google.android.gms.maps.GoogleMap;

public abstract class SuperActivity extends Activity implements
		OnNavigationListener, ConnectionStateListner, OnAltitudeChangedListner, OnSystemArmListener{

	public abstract int getNavigationItem();

	public DroidPlannerApp app;
	public Drone drone;
	private MenuItem connectButton;
	private MenuItem armButton;

	private ScreenOrientation screenOrientation = new ScreenOrientation(this);

	public SuperActivity() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		// Set up the action bar to show a dropdown list.
		setUpActionBar();
		app = (DroidPlannerApp) getApplication();
		app.conectionListner = this;
		app.onSystemArmListener = this;
		this.drone = app.drone;

		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		screenOrientation.unlock();
	}

	public void setUpActionBar() {
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this,
				R.array.menu_dropdown,
				android.R.layout.simple_spinner_dropdown_item);
		actionBar.setListNavigationCallbacks(mSpinnerAdapter, this);
		actionBar.setSelectedNavigationItem(getNavigationItem());
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		if (itemPosition == getNavigationItem()) {
			return false;
		}
		Intent navigationIntent;
		switch (itemPosition) {
		case 0: // Planning
			navigationIntent = new Intent(this, PlanningActivity.class);
			break;
		default:
		case 1: // Flight Data
			navigationIntent = new Intent(this, FlightDataActivity.class);
			navigationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			break;
		case 2: // RC
			navigationIntent = new Intent(this, RCActivity.class);
			break;
		case 3: // Parameters
			navigationIntent = new Intent(this, ParametersActivity.class);
			break;
		case 4: // GCP
			navigationIntent = new Intent(this, GCPActivity.class);
			break;
		case 5: // Chart
			navigationIntent = new Intent(this, ChartActivity.class);
			break;
		case 6: // Camera Feed
			navigationIntent = new Intent(this, CameraFeed.class);
			break;
		case 7://Camera & RC
			navigationIntent = new Intent(this, CameraRC.class);
			break;
			
		}
		getActionBar().setSelectedNavigationItem(getNavigationItem());
		startActivity(navigationIntent);
		return false;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		case R.id.menu_connect:
			drone.MavClient.toggleConnectionState();
			return true;
		case R.id.menu_load_from_apm:
			drone.waypointMananger.getWaypoints();
			return true;
		case R.id.menu_default_alt:
			changeDefaultAlt();
			return true;
		case R.id.menu_preflight_calibration:
			drone.calibrationSetup.startCalibration(this);
			return true;
		case R.id.menu_record_me:
			app.recordMe.toogleRecordMeState();
			return true;
		case R.id.menu_follow_me:
			app.followMe.toogleFollowMeState();
			return true;
		case R.id.menu_map_type_hybrid:
		case R.id.menu_map_type_normal:
		case R.id.menu_map_type_terrain:
		case R.id.menu_map_type_satellite:
			setMapTypeFromItemId(item.getItemId());
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	private void setMapTypeFromItemId(int itemId) {

		final String mapType;
		switch(itemId) {
			case R.id.menu_map_type_hybrid:
				mapType = OfflineMapFragment.MAP_TYPE_HYBRID;
				break;
			case R.id.menu_map_type_normal:
				mapType = OfflineMapFragment.MAP_TYPE_NORMAL;
				break;
			case R.id.menu_map_type_terrain:
				mapType = OfflineMapFragment.MAP_TYPE_TERRAIN;
				break;
			default:
				mapType = OfflineMapFragment.MAP_TYPE_SATELLITE;
				break;
		}

		PreferenceManager.getDefaultSharedPreferences(this).edit()
				.putString(OfflineMapFragment.PREF_MAP_TYPE, mapType)
				.commit();

		drone.notifyMapTypeChanged();
	}

	public void notifyDisconnected() {
		if (connectButton != null) {
			connectButton.setTitle(getResources().getString(
					R.string.menu_connect));
			connectButton.setIcon(R.drawable.connect2);
		}
		if(armButton != null){
			armButton.setEnabled(false);
		}
		screenOrientation.unlock();
	}

	public void notifyConnected() {
		if (connectButton != null) {
			connectButton.setTitle(getResources().getString(
					R.string.menu_disconnect));
			connectButton.setIcon(R.drawable.connect3);
		}
		if(armButton != null){
			armButton.setEnabled(true);
		}
		screenOrientation.requestLock();
	}

	public void notifyArmed() {
		if (armButton != null) {
			armButton.setTitle(getResources().getString(
					R.string.menu_disarm));
		}
	}

	public void notifyDisarmed() {
		if (armButton != null) {
			armButton.setTitle(getResources().getString(
					R.string.menu_arm));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_super_activiy, menu);
		armButton = menu.findItem(R.id.menu_arm);
		connectButton = menu.findItem(R.id.menu_connect);
		drone.MavClient.queryConnectionState();
		return super.onCreateOptionsMenu(menu);
	}

	public void changeDefaultAlt() {
		AltitudeDialog dialog = new AltitudeDialog(this);
		dialog.build(drone.mission.getDefaultAlt(), this);
	}

	@Override
	public void onAltitudeChanged(double newAltitude) {
		drone.mission.setDefaultAlt(newAltitude);
	}
}