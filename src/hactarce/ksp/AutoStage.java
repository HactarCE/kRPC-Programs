package hactarce.ksp;

import krpc.client.RPCException;
import krpc.client.Stream;
import krpc.client.StreamException;
import krpc.client.services.SpaceCenter;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AutoStage extends Subroutine {

	private static final String[] fuels = {
			"LiquidFuel",
			"Oxidizer",
			"SolidFuel"
	};
	private static final float fuelThreshold = 0.1f;
	protected Stream<Integer> currentStage;
	protected Stream<SpaceCenter.Resources> resourcesInDecoupleStage;
	boolean autoStage = false;
	Timer autostageTimer;
	private int streamingStage;

	protected AutoStage(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	public void useStreams(boolean useStreams) throws IOException, RPCException, InterruptedException, StreamException {
		if (useStreams) {
			if (currentStage == null) currentStage = m.connection.addStream(m.control, "getCurrentStage");
			updateResourcesInDecoupleStageStream();
		} else {
			currentStage.remove();
			resourcesInDecoupleStage.remove();
		}
	}

	public List<SpaceCenter.Vessel> stageIfReady() throws IOException, RPCException, InterruptedException, StreamException {
		if (!readyToStage()) return null;
		log("Staging...");
		if (getStage() == 0) setAutoStage(false);
		return m.control.activateNextStage();
	}

	public boolean readyToStage() throws IOException, RPCException, InterruptedException, StreamException {
		if (m.vessel == null) return false;
		if (streamingStage >= getStage()) updateResourcesInDecoupleStageStream();
		for (String fuelType : fuels)
			if ((resourcesInDecoupleStage == null
					? m.vessel.resourcesInDecoupleStage(getStage(), false)
					: resourcesInDecoupleStage.get()
			).amount(fuelType) > fuelThreshold) return false;
		return true;
	}

	private void updateResourcesInDecoupleStageStream() throws IOException, RPCException, InterruptedException, StreamException {
		if (resourcesInDecoupleStage != null) resourcesInDecoupleStage.remove();
		resourcesInDecoupleStage = m.connection.addStream(
				m.vessel, "resourcesInDecoupleStage", streamingStage = getStage(), false);
	}

	public boolean getAutoStage() {
		return autoStage;
	}

	public void setAutoStage(boolean autoStage) throws IOException, RPCException, InterruptedException, StreamException {
		useStreams(autoStage);
		if (!autoStage && this.autoStage) {
			autostageTimer.cancel();
			autostageTimer.purge();
		}
		if (autoStage && !this.autoStage) (autostageTimer = new Timer()).scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				try {
					stageIfReady();
				} catch (IOException | RPCException | InterruptedException | StreamException e) {
					e.printStackTrace();
				}
			}
		}, 200, 100);
		this.autoStage = autoStage;
	}

	public int getStage() throws IOException, RPCException, InterruptedException, StreamException {
		return (currentStage == null ? m.control.getCurrentStage() : currentStage.get());
	}

}
