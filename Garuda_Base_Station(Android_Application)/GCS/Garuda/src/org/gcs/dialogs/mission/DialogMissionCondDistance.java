package org.gcs.dialogs.mission;

import android.view.View;

import org.gcs.R;
import org.gcs.widgets.SeekBarWithText.SeekBarWithText;
import org.gcs.widgets.SeekBarWithText.SeekBarWithText.OnTextSeekBarChangedListner;

public class DialogMissionCondDistance extends DialogMission implements
		OnTextSeekBarChangedListner {
	private SeekBarWithText distanceSeekBar;

	@Override
	protected int getResource() {
		return R.layout.dialog_mission_cond_distance;
	}
	
	protected View buildView() {
		super.buildView();
		distanceSeekBar = (SeekBarWithText) view
				.findViewById(R.id.waypointDistance);
		distanceSeekBar.setValue(wp.missionItem.param1);
		distanceSeekBar.setOnChangedListner(this);

		return view;
	}

	@Override
	public void onSeekBarChanged() {
		wp.missionItem.param1 = (float) distanceSeekBar.getValue();
	}


}
