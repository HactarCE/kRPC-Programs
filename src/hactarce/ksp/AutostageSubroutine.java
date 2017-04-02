package hactarce.ksp;

import krpc.client.Connection;
import krpc.client.RPCException;
import krpc.client.StreamException;
import krpc.client.services.SpaceCenter;

import java.io.IOException;
import java.util.List;

public abstract class AutostageSubroutine extends Subroutine {

	private static final String[] fuels = {
			"LiquidFuel",
			"Oxidizer",
			"SolidFuel"
	};
	private static final float fuelThreshold = 0.1f;

	public AutostageSubroutine(Connection connection) throws IOException, RPCException, InterruptedException, StreamException {
		super(connection);
	}

	protected List<SpaceCenter.Vessel> stageIfReady() throws IOException, RPCException, InterruptedException, StreamException {
		if (!readyToStage()) return null;
		log("Staging...");
		return control.activateNextStage();
	}

	protected boolean readyToStage() throws IOException, RPCException, InterruptedException, StreamException {
		SpaceCenter.Resources resources = vessel.resourcesInDecoupleStage(control.getCurrentStage() - 1, false);
		for (String fuelType : fuels) if (resources.amount(fuelType) > fuelThreshold) return false;
		return true;
	}

}
