package org.gcs.drone.variables;

import org.gcs.drone.Drone;
import org.gcs.drone.DroneVariable;

public class Orientation extends DroneVariable {
	private double roll = 0;
	private double pitch = 0;
	private double yaw = 0;

	public Orientation(Drone myDrone) {
		super(myDrone);
	}

	public double getRoll() {
		return roll;
	}

	public double getPitch() {
		return pitch;
	}

	public double getYaw() {
		return yaw;
	}

	public void setRollPitchYaw(double roll, double pitch, double yaw) {
		this.roll = roll;
		this.pitch = pitch;
		this.yaw = yaw;
		myDrone.notifyHudUpdate();
	}

}