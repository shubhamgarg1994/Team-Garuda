package org.gcs.activitys;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.gcs.R;
import org.gcs.activitys.helpers.SuperActivity;
import org.gcs.dialogs.openfile.OpenFileDialog;
import org.gcs.dialogs.openfile.OpenGcpFileDialog;
import org.gcs.file.IO.GcpReader;
import org.gcs.fragments.GcpMapFragment;
import org.gcs.fragments.GcpMapFragment.OnGcpClickListner;
import org.gcs.fragments.markers.MarkerManager.MarkerSource;
import org.gcs.gcp.Gcp;

import com.google.android.gms.maps.model.LatLng;

public class GCPActivity extends SuperActivity implements OnGcpClickListner {

	public List<Gcp> gcpList;
	private GcpMapFragment gcpMapFragment;

	@Override
	public int getNavigationItem() {
		return 4;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_gcp);

		gcpList = new ArrayList<Gcp>();

		gcpMapFragment = ((GcpMapFragment) getFragmentManager()
				.findFragmentById(R.id.gcpMapFragment));
		clearWaypointsAndUpdate();

		checkIntent();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_gcp, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_clear:
			clearWaypointsAndUpdate();
			return true;
		case R.id.menu_open_kmz:
			openGcpFile();
			return true;
		case R.id.menu_zoom:
			gcpMapFragment.zoomToExtents(getGcpCoordinates());
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	private List<LatLng> getGcpCoordinates() {
		List<LatLng> result = new ArrayList<LatLng>();
		for (Gcp latLng : gcpList) {
			result.add(latLng.coord);
		}
		return result;
	}

	public void openGcpFile() {
		OpenFileDialog dialog = new OpenGcpFileDialog() {
			@Override
			public void onGcpFileLoaded(List<Gcp> list) {
				if (list != null) {
					putListToGcp(list);
				}
			}
		};
		dialog.openDialog(this);
	}

	private void putListToGcp(List<Gcp> list) {
		gcpList.clear();
		gcpList.addAll(list);
		gcpMapFragment.markers.updateMarkers(gcpList, false,getApplicationContext());
		gcpMapFragment.zoomToExtents(getGcpCoordinates());
	}

	private void checkIntent() {
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();
		if (Intent.ACTION_VIEW.equals(action) && type != null) {
			Toast.makeText(this, intent.getData().getPath(), Toast.LENGTH_LONG)
					.show();
			GcpReader parser = (new GcpReader());
			boolean fileIsOpen = parser.openGCPFile(intent.getData().getPath());
			if (fileIsOpen) {
				putListToGcp(parser.gcpList);
			}
		}
	}

	public void clearWaypointsAndUpdate() {
		gcpList.clear();
		gcpMapFragment.markers.clear();
	}

	@Override
	public void onGcpClick(MarkerSource gcp) {
		((org.gcs.gcp.Gcp) gcp).toogleState();
		gcpMapFragment.markers.updateMarker(gcp, false,getApplicationContext());
	}

}
