package org.gcs.activitys;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.gcs.R;
import org.gcs.DroidPlannerApp.OnWaypointUpdateListner;
import org.gcs.activitys.helpers.SuperActivity;
import org.gcs.dialogs.AltitudeDialog.OnAltitudeChangedListner;
import org.gcs.dialogs.openfile.OpenFileDialog;
import org.gcs.dialogs.openfile.OpenMissionDialog;
import org.gcs.drone.variables.waypoint;
import org.gcs.file.IO.MissionReader;
import org.gcs.file.IO.MissionWriter;
import org.gcs.fragments.MissionFragment;
import org.gcs.fragments.PlanningMapFragment;
import org.gcs.fragments.helpers.GestureMapFragment;
import org.gcs.fragments.helpers.MapProjection;
import org.gcs.fragments.helpers.OnMapInteractionListener;
import org.gcs.fragments.helpers.GestureMapFragment.OnPathFinishedListner;
import org.gcs.fragments.survey.SurveyFragment;
import org.gcs.fragments.survey.SurveyFragment.OnNewGridListner;
import org.gcs.helpers.geoTools.PolylineTools;
import org.gcs.helpers.units.Length;
import org.gcs.polygon.Polygon;
import org.gcs.polygon.PolygonPoint;
import org.gcs.survey.grid.Grid;

import com.google.android.gms.maps.model.LatLng;

public class PlanningActivity extends SuperActivity implements
		OnMapInteractionListener, OnWaypointUpdateListner,
		OnAltitudeChangedListner, OnPathFinishedListner, OnNewGridListner {

	public Polygon polygon;
	private PlanningMapFragment planningMapFragment;
	private MissionFragment missionFragment;
	private GestureMapFragment gestureMapFragment;
	private TextView lengthView;
	private SurveyFragment surveyFragment;

	@Override
	public int getNavigationItem() {
		return 0;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_planning);

		planningMapFragment = ((PlanningMapFragment) getFragmentManager()
				.findFragmentById(R.id.planningMapFragment));
		gestureMapFragment = ((GestureMapFragment) getFragmentManager()
				.findFragmentById(R.id.gestureMapFragment));
		missionFragment = (MissionFragment) getFragmentManager()
				.findFragmentById(R.id.missionFragment);
		surveyFragment = (SurveyFragment) getFragmentManager()
				.findFragmentById(R.id.surveyFragment);
		
		lengthView = (TextView) findViewById(R.id.textViewTotalLength);

		polygon = new Polygon();

		gestureMapFragment.setOnPathFinishedListner(this);
		missionFragment.setMission(drone.mission);
		planningMapFragment.setMission(drone.mission);
		surveyFragment.setSurveyData(polygon,drone.mission.getDefaultAlt());
		surveyFragment.setOnSurveyListner(this);
		
		
		drone.mission.missionListner = this;

		checkIntent();

		update();
	}

	private void checkIntent() {
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		if (Intent.ACTION_VIEW.equals(action) && type != null) {
			Toast.makeText(this, intent.getData().getPath(), Toast.LENGTH_LONG)
					.show();
			openMission(intent.getData().getPath());
			update();
			zoom();
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_planning, menu);
		getMenuInflater().inflate(R.menu.menu_map_type, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_zoom:
			zoom();
			return true;
		case R.id.menu_save_file:
			menuSaveFile();
			return true;
		case R.id.menu_open_file:
			openMissionFile();
			return true;
		case R.id.menu_send_to_apm:
			drone.mission.sendMissionToAPM();
			return true;
		case R.id.menu_clear_wp:
			clearWaypointsAndUpdate();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	private void zoom() {
		planningMapFragment
				.zoomToExtents(drone.mission.getAllVisibleCoordinates());
	}

	private void processReceivedPoints(List<LatLng> points) {
		switch (surveyFragment.getPathToDraw()){ 
		case MISSION:
			drone.mission.addWaypointsWithDefaultAltitude(points);			
			break;
		case POLYGON:
			polygon.addPoints(points);
			surveyFragment.generateGrid();
			break;
		default:
			break;
		}
		update();
	}

	private void clearWaypointsAndUpdate() {
		drone.mission.clearWaypoints();
		update();
	}

	private void update() {
		planningMapFragment.update(polygon);
		missionFragment.update();
		updateDistanceView();
	}

	private void updateDistanceView() {
		Length length = PolylineTools.getPolylineLength(drone.mission.getPathPoints());
		lengthView.setText(getString(R.string.length)+": "+ length);		
	}

	@Override
	public void onMapClick(LatLng point) {
		Toast.makeText(this, "Draw your path", Toast.LENGTH_SHORT).show();
		gestureMapFragment.enableGestureDetection();
	}

	@Override
	public void onAddPoint(LatLng point) {
		List<LatLng> points = new ArrayList<LatLng>();
		points.add(point);
		processReceivedPoints(points);
	}

	@Override
	public void onAltitudeChanged(double newAltitude) {
		super.onAltitudeChanged(newAltitude);
		update();
	}

	@Override
	public void onMoveHome(LatLng coord) {
		drone.mission.setHome(coord);
		update();
	}

	@Override
	public void onMoveWaypoint(waypoint source, LatLng latLng) {
		source.setCoord(latLng);
		update();
	}

	@Override
	public void onMovingWaypoint(waypoint source, LatLng latLng)
	{
		updateDistanceView();
		missionFragment.update();
	}

	@Override
	public void onMovePolygonPoint(PolygonPoint source, LatLng newCoord) {
		source.coord = newCoord;
		update();
	}

	@Override
	public void onWaypointsUpdate() {
		update();
		zoom();
	}

	@Override
	public void onPathFinished(List<Point> path) {
		List<LatLng> points = MapProjection.projectPathIntoMap(path,
				planningMapFragment.mMap);
		processReceivedPoints(points);
	}

	private void openMission(String path) {
		MissionReader missionReader = new MissionReader();
		if (missionReader.openMission(path)) {
			drone.mission.setHome(missionReader.getHome());
			drone.mission.setWaypoints(missionReader.getWaypoints());
		}

	}

	private boolean writeMission() {
		MissionWriter missionWriter = new MissionWriter(
				drone.mission.getHome(), drone.mission.getWaypoints());
		return missionWriter.saveWaypoints();
	}

	private void openMissionFile() {
		OpenFileDialog missionDialog = new OpenMissionDialog(drone) {
			@Override
			public void waypointFileLoaded(MissionReader reader) {
				drone.mission.setHome(reader.getHome());
				drone.mission.setWaypoints(reader.getWaypoints());
				zoom();
				update();
			}
		};
		missionDialog.openDialog(this);
	}

	private void menuSaveFile() {
		if (writeMission()) {
			Toast.makeText(this, R.string.file_saved, Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(this, R.string.error_when_saving, Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	public void onNewGrid(Grid grid) {
		drone.mission.setWaypoints(grid.getWaypoints());
		planningMapFragment.cameraOverlays.removeAll();
		if (surveyFragment.isFootPrintOverlayEnabled()) {
			planningMapFragment.cameraOverlays.addOverlays(grid.getCameraLocations(), surveyFragment.getSurveyData());		
		}
		update();		
	}

	@Override
	public void onClearPolygon() {
		polygon.clearPolygon();
		update();		
	}

}
