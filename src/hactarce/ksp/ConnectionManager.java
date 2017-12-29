package hactarce.ksp;

import com.sun.istack.internal.Nullable;
import krpc.client.Connection;
import krpc.client.RPCException;
import krpc.client.Stream;
import krpc.client.StreamException;
import krpc.client.services.KRPC;
import krpc.client.services.SpaceCenter;
import krpc.client.services.UI;

import java.io.IOException;
import java.util.List;

public class ConnectionManager {

	public Stream<Double> ut;
	protected Connection connection;
	protected KRPC krpc;
	protected SpaceCenter spaceCenter;
	protected SpaceCenter.Vessel vessel;
	protected SpaceCenter.AutoPilot auto;
	protected SpaceCenter.Control control;
	protected SpaceCenter.Orbit orbit;
	protected UI ui;
	private boolean isAutoEngaged;

	public ConnectionManager() throws IOException, RPCException, InterruptedException, StreamException {
		this(null);
	}

	public ConnectionManager(@Nullable String s) throws IOException, RPCException, InterruptedException, StreamException {
		connection = s == null ? Connection.newInstance() : Connection.newInstance(s);
		krpc = KRPC.newInstance(connection);
		spaceCenter = SpaceCenter.newInstance(connection);
		vessel = spaceCenter.getActiveVessel();
		auto = vessel.getAutoPilot();
		control = vessel.getControl();
		orbit = vessel.getOrbit();
		ui = UI.newInstance(connection);
		ut = connection.addStream(SpaceCenter.class, "getUT");
	}

	public List<SpaceCenter.Vessel> stage() throws IOException, RPCException {
		List<SpaceCenter.Vessel> vessels = control.activateNextStage();
		vessel = spaceCenter.getActiveVessel();
		return vessels;
	}

	public boolean isAutoEngaged() {
		return isAutoEngaged;
	}

	public void engageAuto() throws IOException, RPCException {
		auto.engage();
		isAutoEngaged = true;
	}

	public void disengageAuto() throws IOException, RPCException {
		auto.disengage();
		isAutoEngaged = false;
	}

	public AutoPilotState saveAutoPilotState() throws IOException, RPCException {
		return new AutoPilotState();
	}

	public class AutoPilotState {

		boolean engaged;
		SpaceCenter.ReferenceFrame referenceFrame;
		boolean sas;
		SpaceCenter.SASMode sasMode;

		private AutoPilotState() throws IOException, RPCException {
			engaged = isAutoEngaged;
			referenceFrame = auto.getReferenceFrame();
			sas = auto.getSAS();
			sasMode = auto.getSASMode();
		}

		public void restore() throws IOException, RPCException {
			if (engaged) engageAuto();
			else disengageAuto();
			auto.setReferenceFrame(referenceFrame);
			auto.setSAS(sas);
			auto.setSASMode(sasMode);
		}

	}

}
