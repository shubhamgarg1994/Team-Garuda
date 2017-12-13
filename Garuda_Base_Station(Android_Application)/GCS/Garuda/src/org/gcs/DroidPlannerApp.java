package org.gcs;

import org.gcs.MAVLink.MavLinkMsgHandler;
import org.gcs.MAVLink.MavLinkStreamRates;
import org.gcs.drone.Drone;
import org.gcs.helpers.FollowMe;
import org.gcs.helpers.RecordMe;
import org.gcs.helpers.TTS;
import org.gcs.service.MAVLinkClient;
import org.gcs.service.MAVLinkClient.OnMavlinkClientListner;

import android.app.Application;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.Messages.ardupilotmega.msg_heartbeat;
import com.MAVLink.Messages.enums.MAV_MODE_FLAG;

public class DroidPlannerApp extends Application implements
		OnMavlinkClientListner {
	public Drone drone;
	private MavLinkMsgHandler mavLinkMsgHandler;
	public FollowMe followMe;
	public RecordMe recordMe;
	public ConnectionStateListner conectionListner;
	public OnSystemArmListener onSystemArmListener;
	private TTS tts;

	public interface OnWaypointUpdateListner {
		public void onWaypointsUpdate();
	}

	public interface ConnectionStateListner {
		public void notifyConnected();
		
		public void notifyDisconnected();
	}

	public interface OnSystemArmListener {
		public void notifyArmed();
		
		public void notifyDisarmed();
	}

	@Override
	public void onCreate() {
		super.onCreate();

		tts = new TTS(this);
		MAVLinkClient MAVClient = new MAVLinkClient(this, this);
		drone = new Drone(tts, MAVClient, getApplicationContext());
		followMe = new FollowMe(this, drone);
		recordMe = new RecordMe(this, drone);
		mavLinkMsgHandler = new org.gcs.MAVLink.MavLinkMsgHandler(
				drone);
	}

	@Override
	public void notifyReceivedData(MAVLinkMessage msg) {
		if(msg.msgid == msg_heartbeat.MAVLINK_MSG_ID_HEARTBEAT){
			msg_heartbeat msg_heart = (msg_heartbeat) msg;
			if((msg_heart.base_mode & (byte) MAV_MODE_FLAG.MAV_MODE_FLAG_SAFETY_ARMED) == (byte) MAV_MODE_FLAG.MAV_MODE_FLAG_SAFETY_ARMED){
				notifyArmed();
			}
			else {
				notifyDisarmed();
			}
		}
		mavLinkMsgHandler.receiveData(msg);
	}

	@Override
	public void notifyDisconnected() {
		conectionListner.notifyDisconnected();
		tts.speak("Disconnected");
	}

	@Override
	public void notifyConnected() {
		MavLinkStreamRates.setupStreamRatesFromPref(this);
		conectionListner.notifyConnected();
		tts.speak("Connected");
	}

	@Override
	public void notifyArmed() {
		onSystemArmListener.notifyArmed();
	}

	@Override
	public void notifyDisarmed() {
		onSystemArmListener.notifyDisarmed();
	}
}
