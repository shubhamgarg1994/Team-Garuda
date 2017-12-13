package org.gcs.dialogs.mission;

import android.view.View;

import org.gcs.R;
import org.gcs.widgets.SeekBarWithText.SeekBarWithText;
import org.gcs.widgets.SeekBarWithText.SeekBarWithText.OnTextSeekBarChangedListner;

public class DialogMissionLand extends DialogMission implements
	OnTextSeekBarChangedListner {
	private SeekBarWithText yawSeekBar;
	
	@Override
	protected int getResource() {
		return R.layout.dialog_mission_land;
	}
	
	protected View buildView() {
		super.buildView();		
		yawSeekBar = (SeekBarWithText) view
			.findViewById(R.id.waypointAngle);
		yawSeekBar.setValue(wp.missionItem.param4);
		yawSeekBar.setOnChangedListner(this);
	return view;
	
	}

	@Override
	public void onSeekBarChanged() {
		wp.missionItem.param4 = (float) yawSeekBar.getValue();
	}
}
