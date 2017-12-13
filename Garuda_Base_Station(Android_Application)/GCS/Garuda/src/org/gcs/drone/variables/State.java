package org.gcs.drone.variables;

import org.gcs.MAVLink.MavLinkModes;
import org.gcs.drone.Drone;
import org.gcs.drone.DroneVariable;

import com.MAVLink.Messages.ApmModes;

public class State extends DroneVariable {
	private boolean failsafe = false;
	private boolean armed = false;
	private ApmModes mode = ApmModes.UNKNOWN;

	public State(Drone myDrone) {
		super(myDrone);
	}

	public boolean isFailsafe() {
		return failsafe;
	}

	public boolean isArmed() {
		return armed;
	}

	public ApmModes getMode() {
		return mode;
	}

	public void setArmedAndFailsafe(boolean armed, boolean failsafe) {
		if (this.armed != armed | this.failsafe != failsafe) {
			if (this.armed != armed) {
				myDrone.tts.speakArmedState(armed);
			}
			this.armed = armed;
			this.failsafe = failsafe;
			myDrone.notifyHudUpdate();
		}
	}

	public void setMode(ApmModes mode) {
		if (this.mode != mode) {
			this.mode = mode;
			myDrone.tts.speakMode(mode);
			myDrone.notifyModeChanged();
			myDrone.notifyHudUpdate();
		}
	}

	public void changeFlightMode(ApmModes mode) {
		if (ApmModes.isValid(mode)) {
			MavLinkModes.changeFlightMode(myDrone, mode);
		}
	}
}