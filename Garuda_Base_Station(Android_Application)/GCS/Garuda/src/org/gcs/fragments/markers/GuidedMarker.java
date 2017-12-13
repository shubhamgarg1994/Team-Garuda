package org.gcs.fragments.markers;

import org.gcs.drone.variables.GuidedPoint;
import org.gcs.fragments.markers.helpers.MarkerWithText;

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GuidedMarker {
	public static MarkerOptions build(GuidedPoint guidedPoint, Double altitude, Context context) {
		return new MarkerOptions()
				.position(guidedPoint.getCoord())
				.icon(getIcon(guidedPoint, altitude, context));
	}

	public static void update(Marker marker, GuidedPoint guidedPoint, Double altitude, Context context) {
		marker.setPosition(guidedPoint.getCoord());
		marker.setIcon(getIcon(guidedPoint, altitude, context));
	}

	private static BitmapDescriptor getIcon(GuidedPoint guidedPoint, double altitude, Context context)
	{
		return BitmapDescriptorFactory.fromBitmap(MarkerWithText.getMarkerWithTextAndDetail(Color.YELLOW,
			"#", String.format("%.0fm", altitude), context));
	}
}