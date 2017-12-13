package org.gcs.drone;


import org.gcs.drone.DroneInterfaces.DroneTypeListner;
import org.gcs.drone.DroneInterfaces.HudUpdatedListner;
import org.gcs.drone.DroneInterfaces.MapConfigListener;
import org.gcs.drone.DroneInterfaces.MapUpdatedListner;
import org.gcs.drone.DroneInterfaces.ModeChangedListener;
import org.gcs.drone.variables.Altitude;
import org.gcs.drone.variables.Battery;
import org.gcs.drone.variables.Calibration;
import org.gcs.drone.variables.GPS;
import org.gcs.drone.variables.GuidedPoint;
import org.gcs.drone.variables.Mission;
import org.gcs.drone.variables.Orientation;
import org.gcs.drone.variables.Parameters;
import org.gcs.drone.variables.Speed;
import org.gcs.drone.variables.State;
import org.gcs.drone.variables.Type;
import org.gcs.drone.variables.WaypointMananger;
import org.gcs.helpers.TTS;
import org.gcs.service.MAVLinkClient;

import android.content.Context;

public class Drone {
	public Type type = new Type(this);
	public GPS GPS = new GPS(this);
	public Speed speed = new Speed(this);
	public State state = new State(this);
	public Battery battery = new Battery(this);
	public Mission mission = new Mission(this);
	public Altitude altitude = new Altitude(this);
	public Orientation orientation = new Orientation(this);
	public GuidedPoint guidedPoint = new GuidedPoint(this);
	public Parameters parameters = new Parameters(this);
	public Calibration calibrationSetup = new Calibration(this);
	public WaypointMananger waypointMananger = new WaypointMananger(this);

	public TTS tts;
	public MAVLinkClient MavClient;
	public Context context;

	private HudUpdatedListner hudListner;
	private MapUpdatedListner mapListner;
	private MapConfigListener mapConfigListener;
	private DroneTypeListner typeListner;
	private ModeChangedListener modeChangedListener;

	public Drone(TTS tts, MAVLinkClient mavClient, Context context) {
		this.tts = tts;
		this.MavClient = mavClient;
		this.context = context;
	}

	public void setHudListner(HudUpdatedListner listner) {
		hudListner = listner;
	}

	public void setMapListner(MapUpdatedListner listner) {
		mapListner = listner;
	}

	public void setMapConfigListener(MapConfigListener mapConfigListener) {
		this.mapConfigListener = mapConfigListener;
	}

	public void setDroneTypeChangedListner(DroneTypeListner listner) {
		typeListner = listner;
	}

	public void setModeChangedListener(ModeChangedListener listener)
	{
		this.modeChangedListener = listener;
	}

	public void setAltitudeGroundAndAirSpeeds(double altitude,
			double groundSpeed, double airSpeed, double climb) {
		this.altitude.setAltitude(altitude);
		speed.setGroundAndAirSpeeds(groundSpeed, airSpeed, climb);
		notifyHudUpdate();
	}

	public void setDisttowpAndSpeedAltErrors(double disttowp, double alt_error,
			double aspd_error) {
		mission.setDistanceToWp(disttowp);
		altitude.setAltitudeError(alt_error);
		speed.setSpeedError(aspd_error);
		notifyHudUpdate();
	}

	public void notifyPositionChange() {
		if (mapListner != null) {
			mapListner.onDroneUpdate();
		}
	}

	public void notifyTypeChanged() {
		if (typeListner != null) {
			typeListner.onDroneTypeChanged();
		}
	}

	public void notifyHudUpdate() {
		if (hudListner != null)
			hudListner.onDroneUpdate();
	}

	public void notifyMapTypeChanged() {
		if (mapConfigListener != null)
			mapConfigListener.onMapTypeChanged();
	}

	public void notifyModeChanged()
	{
		if (modeChangedListener != null)
			modeChangedListener.onModeChanged();
	}
}
