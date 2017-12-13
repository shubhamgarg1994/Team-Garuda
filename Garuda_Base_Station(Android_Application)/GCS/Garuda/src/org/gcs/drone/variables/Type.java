package org.gcs.drone.variables;

import org.gcs.drone.Drone;
import org.gcs.drone.DroneVariable;

import com.MAVLink.Messages.enums.MAV_TYPE;

public class Type extends DroneVariable {
	private int type = MAV_TYPE.MAV_TYPE_FIXED_WING;

	public Type(Drone myDrone) {
		super(myDrone);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		if (this.type != type) {
			this.type = type;
			myDrone.notifyTypeChanged();
		}
	}
}