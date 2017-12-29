package hactarce.ksp;

import com.sun.javafx.util.Utils;
import krpc.client.RPCException;
import krpc.client.Stream;
import krpc.client.StreamException;
import krpc.client.services.SpaceCenter;

import java.io.IOException;

public class SimpleOrbiter extends Subroutine {

	public static float turnStartSpeed = 50;
	public static float orbitAlt = 100000;
	public static double verticalAscentEnd = 200;
	public static double turnEnd = 60000;
	public static double turnEndAngle = 0;
	public static double turnShapeExponent = .387;

	public SimpleOrbiter(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	public void launchToOrbit() throws IOException, RPCException, InterruptedException, StreamException {

		AutoStage autoStage = new AutoStage(m);

		SpaceCenter.ReferenceFrame planetRefFrame = m.vessel.getOrbit().getBody().getReferenceFrame();
		m.vessel.direction(planetRefFrame);
		SpaceCenter.Flight planetFlight = m.vessel.flight(planetRefFrame);
		SpaceCenter.Flight vesselFlight = m.vessel.flight(m.vessel.getSurfaceReferenceFrame());
		Streamable<Float> maxThrust = new Streamable<>(
				() -> m.connection.addStream(m.vessel, "getMaxThrust"),
				m.vessel::getMaxThrust
		).open();
		Streamable<Float> shipMass = new Streamable<>(
				() -> m.connection.addStream(m.vessel, "getMass"),
				m.vessel::getMass
		).open();
		Streamable<Double> altitude = new Streamable<>(() -> m.connection.addStream(planetFlight, "getMeanAltitude"), planetFlight::getMeanAltitude);
//		Stream<Double> altitude = m.connection.addStream(planetFlight, "getMeanAltitude");
		Stream<Double> apoapsis = m.connection.addStream(m.vessel.getOrbit(), "getApoapsisAltitude");
		Calculator<Float> maxAcceleration = () -> maxThrust.get() / shipMass.get();
		Calculator<Float> throttleMultiplier = () -> (float) Utils.clamp(0, (orbitAlt - apoapsis.get()) / maxAcceleration.get() * 2, 1);
		float atmoHeight = m.vessel.getOrbit().getBody().getAtmosphereDepth();
		m.auto.targetPitchAndHeading(90, 90);
		m.engageAuto();
		log("Target locked");
		boolean stageNeeded = m.vessel.getAvailableThrust() == 0;
		if (!stageNeeded) countdown();
		float surfaceGravity = m.vessel.getOrbit().getBody().getSurfaceGravity();
		Calculator<Float> maxTWR = () -> maxThrust.get() / shipMass.get() / surfaceGravity;
		Calculator<Float> targetTWR = () -> altitude.get() / atmoHeight >= 4 / 7 ? 999 : (float) (1.25 - Math.log(4 / 7 - altitude.get() / atmoHeight));
		setThrottle(1.5f / maxTWR.get());
		log("Throttle set to 1.5 TWR (%.1f%%)", 150 / maxTWR.get());
		if (stageNeeded) {
			countdown();
			m.control.activateNextStage();
			log("Liftoff!");
		}
		log("Turning on autostaging...");
		autoStage.setAutoStage(true);
		Streamable<Double> speed = new Streamable<>(
				() -> m.connection.addStream(planetFlight, "getSpeed"),
				planetFlight::getSpeed
		).open();
		while (speed.get() < turnStartSpeed) {
			System.out.println("altitude.get() / atmoHeight = " + altitude.get() / atmoHeight);
			System.out.println("targetTWR.get() = " + targetTWR.get());
			setThrottle(targetTWR.get() / maxTWR.get());
		}
		speed.close();
		log("Starting gravity turn...");
		Calculator<Double> targetPitch = () -> Utils.clamp(0.01, (float) (90.0 - Math.pow((altitude.get() - verticalAscentEnd) / (turnEnd - verticalAscentEnd), turnShapeExponent) * (90.0 - turnEndAngle)), 89.99);
		altitude.open();
		Calculator<Double> gravityTurnPitch = () -> {
			double x = altitude.get() / atmoHeight;
			return 146.71 * Math.pow(x, 1.5) + -472.5 * x + 90;
		};
		while (altitude.get() < 4 * atmoHeight / 7) {
			double t = m.ut.get();
			m.auto.targetPitchAndHeading((float) (double) gravityTurnPitch.get(), 90f);
			setThrottle(targetTWR.get() / maxTWR.get() * throttleMultiplier.get());
			sleepUntil(t + 0.25);
		}
		log("Gravity turn complete");
		m.auto.targetPitchAndHeading(90, 0);
		m.auto.engage();
		while (altitude.get() < atmoHeight && apoapsis.get() < orbitAlt) setThrottle(throttleMultiplier.get());
		log("Planning circularization maneuver");
		new NodeTools(m, new NodeBuilder(m).circularizeAtAp()).execNode();
		if (m.vessel.getOrbit().getPeriapsisAltitude() <= atmoHeight)
			log("Orbit unsuccessful");
		else {
			log("Orbit achieved!");
			log("Apoapsis        %.0d", apoapsis.get());
			log("Periapsis       %.0d", m.vessel.getOrbit().getPeriapsisAltitude());
			log("Eccentricity    %.5d", m.vessel.getOrbit().getEccentricity());
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
