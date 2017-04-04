package hactarce.ksp;

import hactarce.Utils;
import krpc.client.Connection;
import krpc.client.RPCException;
import krpc.client.Stream;
import krpc.client.StreamException;
import krpc.client.services.SpaceCenter;

import java.io.IOException;

public class SimpleOrbiter extends AutostageSubroutine {

	public static float turnStartAlt = 250;
	//	public static float turnEndAlt = 45000;
	public static float orbitAlt = 100000;

	public SimpleOrbiter(Connection connection) throws IOException, RPCException, InterruptedException, StreamException {
		super(connection);
	}

	@Override
	public void execute() throws IOException, RPCException, InterruptedException, StreamException {

		SpaceCenter.ReferenceFrame surfaceRefFrame = vessel.getSurfaceReferenceFrame();
		SpaceCenter.Flight flight = vessel.flight(surfaceRefFrame);

		Stream<Double> altitude = connection.addStream(flight, "getMeanAltitude");
		Stream<Double> apoapsis = connection.addStream(vessel.getOrbit(), "getApoapsisAltitude");
		Stream<Float> maxThrust = connection.addStream(vessel, "getMaxThrust");
		Stream<Float> shipMass = connection.addStream(vessel, "getMass");
		float atmoHeight = vessel.getOrbit().getBody().getAtmosphereDepth();
		boolean stageNeeded = vessel.getAvailableThrust() == 0;
		if (!stageNeeded) countdown();
		auto.targetPitchAndHeading(90, 90);
		auto.engage();
		log("Target locked");
		control.setThrottle(1);
		log("Throttle set to 100%%");
		if (stageNeeded) {
			countdown();
			control.activateNextStage();
			log("Liftoff!");
		}
		log("Turning on AUTOSTAGE");
		setAutostage(true);

		int launchStep = 0;
		double calculatedTurnAngle = 0;
		double targetTurnAngle = 0;
		float calculatedThrottle = 1;
		float realThrottle = 1;
		while (atmoHeight == 0 ? apoapsis.get() < orbitAlt : altitude.get() < atmoHeight) {

			double alt = altitude.get();

			switch (launchStep) {
				case 0:
					if (alt > turnStartAlt) {
						log("Beginning gravity turn...");
						launchStep = 1;
					}
					break;
				case 1:
					if (apoapsis.get() >= orbitAlt) {
						log("Coasting until end of atmosphere...");
						spaceCenter.setPhysicsWarpFactor(1);
						launchStep = 2;
					}
					break;
			}

			if (alt < turnStartAlt) calculatedTurnAngle = 0;
//			else if (alt > turnEndAlt) calculatedTurnAngle = 90;
//			else calculatedTurnAngle = (alt - turnStartAlt) * 90 / (turnEndAlt - turnStartAlt);
			else calculatedTurnAngle = 90 - (orbitAlt - apoapsis.get()) * 90 / orbitAlt;
			// TODO: 4/2/2017 Move this into its own method (maybe create a special AttitudeController class?)
			if (Math.abs(targetTurnAngle - calculatedTurnAngle) > 0.5)
//				auto.targetPitchAndHeading((float) (90 - (targetTurnAngle = calculatedTurnAngle)), 90);
				auto.targetPitchAndHeading((float) (90 - (targetTurnAngle = calculatedTurnAngle)), 0);

			float maxAcceleration = maxThrust.get() / shipMass.get();
			calculatedThrottle = (float) Utils.range((orbitAlt - apoapsis.get()) / maxAcceleration * 2, 0, 1);
			// TODO: 4/2/2017 Move this into its own method
			if ((realThrottle == 0 ^ calculatedThrottle == 0)
//					|| (realThrottle != 0 && calculatedThrottle != 0 && Math.abs(Math.log(realThrottle) - Math.log(calculatedThrottle)) < 0.01))
					|| realThrottle != calculatedThrottle)
				control.setThrottle(realThrottle = calculatedThrottle);

		}
		control.setThrottle(0);
		spaceCenter.setPhysicsWarpFactor(0);

		if (!vessel.getParts().getFairings().isEmpty()) {
			log("Detaching fairings");
			for (SpaceCenter.Fairing fairing : vessel.getParts().getFairings()) fairing.jettison();
		}

		log("Planning circularization burn");
		SpaceCenter.Node node = addCircularizationNodeAtAp();
		executeNode(node);

		if (vessel.getOrbit().getPeriapsisAltitude() <= atmoHeight)
			log("Orbit unsuccessful");
		else {
			log("Orbit achieved!");
			log("Apoapsis        %.0d", apoapsis.get());
			log("Periapsis       %.0d", vessel.getOrbit().getPeriapsisAltitude());
			log("Eccentricity    %.5d", vessel.getOrbit().getEccentricity());
		}

	}

	private void countdown() throws IOException, RPCException, InterruptedException, StreamException {
		log("Commencing counting");
		log("3...");
		sleep(1);
		log("2...");
		sleep(1);
		log("1...");
		sleep(1);
	}

}
