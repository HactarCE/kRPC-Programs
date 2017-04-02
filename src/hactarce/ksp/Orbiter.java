package hactarce.ksp;

import krpc.client.Connection;
import krpc.client.RPCException;
import krpc.client.Stream;
import krpc.client.StreamException;
import krpc.client.services.KRPC;
import krpc.client.services.SpaceCenter;

import java.io.IOException;

public class Orbiter extends AutostageSubroutine {

	public Orbiter(Connection connection) throws IOException, RPCException, InterruptedException, StreamException {
		super(connection);
	}

	@Override
	public void execute() throws IOException, RPCException, InterruptedException, StreamException {
		SpaceCenter.ReferenceFrame surfaceRefFrame = vessel.getSurfaceReferenceFrame();
		auto.targetPitchAndHeading(90, 90);
		auto.engage();
		log("Target locked");
		control.setThrottle(1);
		log("Throttle set to 100%");
		SpaceCenter.Flight flight = vessel.flight(surfaceRefFrame);
	}

	private void countdown() throws IOException, RPCException, InterruptedException, StreamException {
		log("Commencing counting");
		log("3...");
		sleep(1);
		log("2...");
		sleep(1);
		log("1...");
		sleep(1);
		log("Liftoff!");
	}

}
