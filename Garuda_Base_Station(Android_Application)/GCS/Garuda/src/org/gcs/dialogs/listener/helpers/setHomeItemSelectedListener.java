package org.gcs.dialogs.listener.helpers;

import org.gcs.dialogs.mission.DialogMissionSetHome;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class setHomeItemSelectedListener implements OnItemSelectedListener {
	DialogMissionSetHome dm;

	public setHomeItemSelectedListener(DialogMissionSetHome mdm) {
		dm = mdm;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		dm.setHome(arg2);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}