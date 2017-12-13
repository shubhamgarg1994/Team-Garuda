package org.gcs.drone;

import java.util.List;

import org.gcs.parameters.Parameter;

public class DroneInterfaces {
	public interface MapUpdatedListner {
		public void onDroneUpdate();
	}

	public interface MapConfigListener {
		public void onMapTypeChanged();
	}

	public interface DroneTypeListner {
		public void onDroneTypeChanged();
	}

	public interface HudUpdatedListner {
		public void onDroneUpdate();
	}

	public interface ModeChangedListener {
		public void onModeChanged();
	}

	public interface OnParameterManagerListner {
		public void onBeginReceivingParameters();
		public void onParameterReceived(Parameter parameter, int index, int count);
		public void onEndReceivingParameters(List<Parameter> parameter);
        public void onParamterMetaDataChanged();
	}
}