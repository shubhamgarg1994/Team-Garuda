package org.gcs.activitys;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.MAVLink.Messages.ApmModes;

import org.gcs.R;
import org.gcs.DroidPlannerApp.OnWaypointUpdateListner;
import org.gcs.activitys.helpers.SuperFlightActivity;
import org.gcs.drone.DroneInterfaces.ModeChangedListener;
import org.gcs.fragments.FlightMapFragment;
import org.gcs.fragments.helpers.GuidePointListener;
import org.gcs.helpers.geoTools.GeoTools;
import org.gcs.helpers.units.Length;

import com.google.android.gms.maps.model.LatLng;

public class FlightDataActivity extends SuperFlightActivity implements
		OnWaypointUpdateListner, ModeChangedListener, GuidePointListener {
	private TextView distanceView;

	@Override
	public int getNavigationItem() {
		return 1;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_flightdata);

		mapFragment = ((FlightMapFragment) getFragmentManager()
				.findFragmentById(R.id.flightMapFragment));

		distanceView = (TextView) findViewById(R.id.textViewDistance);
		mapFragment.setGuidePointListener(this);
		mapFragment.updateFragment();

		drone.mission.missionListner = this;
		drone.setDroneTypeChangedListner(this);
		drone.setModeChangedListener(this);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_flightdata, menu);
		getMenuInflater().inflate(R.menu.menu_map_type, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_zoom:
			mapFragment.zoomToLastKnowPosition();
			return true;
		case R.id.menu_clearFlightPath:
			mapFragment.clearFlightPath();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	@Override
	public void onAltitudeChanged(double newAltitude) {
		super.onAltitudeChanged(newAltitude);
		mapFragment.updateFragment();
	}

	@Override
	public void onModeChanged() {
		mapFragment.onModeChanged();
		checkDistanceVisible();
	}

	@Override
	public void OnGuidePointMoved() {
		updateDistanceView();
	}

	private void updateDistanceView() {
		final Location myLoc = mapFragment.mMap.getMyLocation();
		if(myLoc != null) {
			Length distance = new Length(GeoTools.getDistance(drone.guidedPoint.getCoord(),
			                                                  new LatLng(myLoc.getLatitude(), myLoc.getLongitude())));
			distanceView.setText(getString(R.string.length) + ": " + distance);
		}
		else {
			new Handler().postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					updateDistanceView();
				}
			}, 2000);
		}
		checkDistanceVisible();
	}

	private void checkDistanceVisible() {
		distanceView.setVisibility(drone.guidedPoint.isCoordValid() && distanceView.getText().length() > 0 ? View.VISIBLE : View.INVISIBLE);
	}
}
