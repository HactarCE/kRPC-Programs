package hactarce.ksp;

import krpc.client.Connection;
import krpc.client.RPCException;
import krpc.client.Stream;
import krpc.client.StreamException;
import krpc.client.services.SpaceCenter;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class AutostageSubroutine extends Subroutine {

	private static final String[] fuels = {
			"LiquidFuel",
			"Oxidizer",
			"SolidFuel"
	};
	private static final float fuelThreshold = 0.1f;
	protected Stream<SpaceCenter.Resources> resourcesInDecoupleStage;
	protected Stream<Integer> currentStage;
	boolean autostage = false;
	Timer autostageTimer;
	private int streamStage;

	public AutostageSubroutine(Connection connection) throws IOException, RPCException, InterruptedException, StreamException {
		super(connection);
		currentStage = connection.addStream(control, "getCurrentStage");
		addResourcesInDecoupleStageStream();
	}

	protected List<SpaceCenter.Vessel> stageIfReady() throws IOException, RPCException, InterruptedException, StreamException {
		if (!readyToStage()) return null;
		log("Autostaging...");
		if (currentStage.get() == 1) setAutostage(false);
		return control.activateNextStage();
	}

	protected boolean readyToStage() throws IOException, RPCException, InterruptedException, StreamException {
		if (streamStage >= control.getCurrentStage()) addResourcesInDecoupleStageStream();
		for (String fuelType : fuels) if (resourcesInDecoupleStage.get().amount(fuelType) > fuelThreshold) return false;
		return true;
	}

	private void addResourcesInDecoupleStageStream() throws IOException, RPCException, InterruptedException, StreamException {
		if (resourcesInDecoupleStage != null) resourcesInDecoupleStage.remove();
		resourcesInDecoupleStage = connection.addStream(vessel, "resourcesInDecoupleStage", streamStage = currentStage.get(), false);
	}

	public boolean getAutostage() {
		return autostage;
	}

	public void setAutostage(boolean autostage) {
		if (!autostage && this.autostage) {
			autostageTimer.cancel();
			autostageTimer.purge();
		}
		if (autostage && !this.autostage) (autostageTimer = new Timer()).scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				try {
					stageIfReady();
				} catch (IOException | RPCException | InterruptedException | StreamException e) {
					e.printStackTrace();
				}
			}
		}, 200, 100);
		this.autostage = autostage;
	}

}
