package org.gcs.dialogs.mission;

import android.view.View;

import org.gcs.R;
import org.gcs.widgets.SeekBarWithText.SeekBarWithText;
import org.gcs.widgets.SeekBarWithText.SeekBarWithText.OnTextSeekBarChangedListner;

public class DialogMissionGeneric extends DialogMission implements
		OnTextSeekBarChangedListner {
	private SeekBarWithText altitudeSeekBar;

	@Override
	protected int getResource() {
		return R.layout.dialog_mission_generic;
	}
	
	protected View buildView() {
		super.buildView();
		altitudeSeekBar = (SeekBarWithText) view
				.findViewById(R.id.altitudeView);
		altitudeSeekBar.setValue(wp.getHeight());
		altitudeSeekBar.setOnChangedListner(this);

		return view;
	}

	@Override
	public void onSeekBarChanged() {
		wp.setHeight(altitudeSeekBar.getValue());
	}


}
