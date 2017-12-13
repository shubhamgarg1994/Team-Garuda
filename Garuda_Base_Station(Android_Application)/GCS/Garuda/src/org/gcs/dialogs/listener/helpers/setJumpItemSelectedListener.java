package org.gcs.dialogs.listener.helpers;

import org.gcs.dialogs.mission.DialogMissionSetJump;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class setJumpItemSelectedListener implements OnItemSelectedListener {
	DialogMissionSetJump dm;

	public setJumpItemSelectedListener(DialogMissionSetJump mdm) {
		dm = mdm;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		dm.setJump(arg2);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}