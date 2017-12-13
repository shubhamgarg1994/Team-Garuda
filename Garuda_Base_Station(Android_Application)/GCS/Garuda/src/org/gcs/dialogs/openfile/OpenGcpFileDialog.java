package org.gcs.dialogs.openfile;

import java.util.List;

import org.gcs.file.IO.GcpReader;
import org.gcs.gcp.Gcp;

public abstract class OpenGcpFileDialog extends OpenFileDialog {
	public abstract void onGcpFileLoaded(List<Gcp> gcpList);

	@Override
	protected FileReader createReader() {
		return new GcpReader();
	}

	@Override
	protected void onDataLoaded(FileReader reader) {
		onGcpFileLoaded(((GcpReader) reader).gcpList);
	}
}
