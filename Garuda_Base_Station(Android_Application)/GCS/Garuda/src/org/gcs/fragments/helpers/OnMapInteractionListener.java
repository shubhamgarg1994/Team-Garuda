package org.gcs.fragments.helpers;

import org.gcs.drone.variables.waypoint;
import org.gcs.polygon.PolygonPoint;

import com.google.android.gms.maps.model.LatLng;

public interface OnMapInteractionListener {

	public void onAddPoint(LatLng point);

	public void onMoveHome(LatLng coord);

	public void onMoveWaypoint(waypoint waypoint, LatLng latLng);

	public void onMovingWaypoint(waypoint source, LatLng latLng);

	public void onMovePolygonPoint(PolygonPoint source, LatLng newCoord);

	public void onMapClick(LatLng point);
}