package org.gcs.activitys.helpers;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.MAVLink.Messages.ApmModes;

import org.gcs.R;
import org.gcs.DroidPlannerApp.OnWaypointUpdateListner;
import org.gcs.MAVLink.MavLinkArm;
import org.gcs.drone.DroneInterfaces.DroneTypeListner;
import org.gcs.drone.variables.GuidedPoint;
import org.gcs.drone.variables.GuidedPoint.OnGuidedListener;
import org.gcs.fragments.FlightMapFragment;
import org.gcs.fragments.HudFragment;
import org.gcs.widgets.spinners.SelectModeSpinner;
import org.gcs.widgets.spinners.SelectWaypointSpinner;
import org.gcs.widgets.spinners.SelectModeSpinner.OnModeSpinnerSelectedListener;
import org.gcs.widgets.spinners.SelectWaypointSpinner.OnWaypointSpinnerSelectedListener;

public abstract class SuperFlightActivity extends SuperActivity implements
		OnModeSpinnerSelectedListener, OnWaypointSpinnerSelectedListener,
		OnGuidedListener, DroneTypeListner, OnWaypointUpdateListner {


	private SelectModeSpinner fligthModeSpinner;
	private SelectWaypointSpinner wpSpinner;

	public FlightMapFragment mapFragment;
	public HudFragment hudFragment;

	public SuperFlightActivity() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		drone.guidedPoint.setOnGuidedListner(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_super_flight_activity, menu);

		MenuItem flightModeMenu = menu.findItem(R.id.menu_flight_modes_spinner);
		fligthModeSpinner = (SelectModeSpinner) flightModeMenu.getActionView();
		fligthModeSpinner.buildSpinner(this, this);
		fligthModeSpinner.updateModeSpinner(drone);

		MenuItem wpMenu = menu.findItem(R.id.menu_wp_spinner);
		wpSpinner = (SelectWaypointSpinner) wpMenu.getActionView();
		wpSpinner.buildSpinner(this, this);

		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_arm:
			if (drone.MavClient.isConnected()) {
				if (!drone.state.isArmed())
					drone.tts.speak("Arming the vehicle, please standby");
				MavLinkArm.sendArmMessage(drone, !drone.state.isArmed());

			}
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	@Override
	public void OnModeSpinnerSelected(String text) {
		ApmModes mode = ApmModes.getMode(text, drone.type.getType());
		if (ApmModes.isValid(mode)) {
			drone.state.changeFlightMode(mode);
		}
	}

	@Override
	public void OnWaypointSpinnerSelected(int item) {
		drone.waypointMananger.setCurrentWaypoint((short) item);
	}

	@Override
	public void onGuidedPoint(GuidedPoint guidedPoint) {
		changeDefaultAlt();
	}

	@Override
	public void onAltitudeChanged(double newAltitude) {
		super.onAltitudeChanged(newAltitude);
		if (drone.guidedPoint.isCoordValid()) {
			drone.guidedPoint.setGuidedMode();
		}
	}

	@Override
	public void onDroneTypeChanged() {
		Log.d("DRONE", "Drone type changed");
		fligthModeSpinner.updateModeSpinner(drone);
		if (mapFragment != null) {
			mapFragment.droneMarker.updateDroneMarkers();
		}
	}

	@Override
	public void onWaypointsUpdate() {
		wpSpinner.updateWpSpinner(drone);
		if (mapFragment != null) {
			mapFragment.updateFragment();
		}
	}
}
