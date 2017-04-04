package hactarce.ksp;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import hactarce.Utils;
import krpc.client.Connection;
import krpc.client.RPCException;
import krpc.client.Stream;
import krpc.client.StreamException;
import krpc.client.services.KRPC;
import krpc.client.services.SpaceCenter;
import org.javatuples.Triplet;

import java.io.IOException;

public abstract class Subroutine {

	protected static final double maneuverLeadTime = 5;
	protected static final double defaultNodePrecision = 0.05;

	public Connection connection;
	protected KRPC krpc;
	protected SpaceCenter spaceCenter;
	protected SpaceCenter.Vessel vessel;
	protected SpaceCenter.AutoPilot auto;
	protected SpaceCenter.Control control;

	protected Stream<Double> ut;

	public Subroutine(Connection connection) throws IOException, RPCException, InterruptedException, StreamException {
		log("Initializing %s subroutine...", getClass().getSimpleName());
		this.connection = connection;
		krpc = KRPC.newInstance(connection);
		spaceCenter = SpaceCenter.newInstance(connection);
		vessel = spaceCenter.getActiveVessel();
		auto = vessel.getAutoPilot();
		control = vessel.getControl();
		ut = connection.addStream(SpaceCenter.class, "getUT");
	}

	//region static Maneuvers (because IntelliJ rearranges code)
	private static double getOrbitalVelocity(SpaceCenter.CelestialBody body, double radius) throws IOException, RPCException, InterruptedException, StreamException {
		return Math.sqrt(body.getGravitationalParameter() / radius);
	}

	//endregion

	public abstract void execute() throws IOException, RPCException, InterruptedException, StreamException;

	//region Maneuvers
	// TODO: 4/2/2017 Generalize circularization maneuver to any point in orbit (or at least Ap/Pe)
	public SpaceCenter.Node addCircularizationNodeAtAp() throws IOException, RPCException, InterruptedException, StreamException {
		return control.addNode(ut.get() + vessel.getOrbit().getTimeToApoapsis(),
				(float) (getOrbitalVelocity(vessel.getOrbit().getBody(), vessel.getOrbit().getApoapsis()) - velocityAtAp()),
				0, 0);
	}

	public double velocityAtAp() throws IOException, RPCException, InterruptedException, StreamException {
		SpaceCenter.Orbit orbit = vessel.getOrbit();
		double mu = orbit.getBody().getGravitationalParameter();
		return Math.sqrt(mu * ((2.0 / orbit.getApoapsis()) - (1.0 / orbit.getSemiMajorAxis())));
	}

	public void executeNode() throws IOException, RPCException, InterruptedException, StreamException {
		executeNode(control.getNodes().get(0));
	}

	public void executeNode(SpaceCenter.Node node) throws IOException, RPCException, InterruptedException, StreamException {
		executeNode(node, defaultNodePrecision);
	}

	public void executeNode(SpaceCenter.Node node, double precision) throws IOException, RPCException, InterruptedException, StreamException {
		double burnTime = simpleBurnTime(node);
		auto.setReferenceFrame(node.getReferenceFrame());
		auto.setTargetDirection(new Triplet<>(0.0, 1.0, 0.0));
		auto.engage();
		auto.wait_();
		double burnUt = node.getUT() - (burnTime / 2.0);
		log("Warping to maneuver...");
		if (ut.get() < burnUt) {
			spaceCenter.warpTo(burnUt - maneuverLeadTime, 100000, 1);
			sleepUntil(burnUt);
		}
		log("Executing maneuver...");
		Stream<Float> maxThrust = connection.addStream(vessel, "getMaxThrust");
		Stream<Float> shipMass = connection.addStream(vessel, "getMass");
		Stream<Double> remainingDeltaV = connection.addStream(node, "getRemainingDeltaV");
		Stream<SpaceCenter.ReferenceFrame> nodeReferenceFrame = connection.addStream(node, "getReferenceFrame");
		float calculatedThrottle = 0;
		float realThrottle = 0;
		while (remainingDeltaV.get() > precision) {
			auto.setReferenceFrame(nodeReferenceFrame.get());
			auto.setTargetDirection(new Triplet<>(0.0, 1.0, 0.0));
			// TODO: 4/2/2017 Stop burning temporarily if off-target
			float maxAcceleration = maxThrust.get() / shipMass.get();
			calculatedThrottle = (float) Math.min(remainingDeltaV.get() / maxAcceleration, 1);
			// TODO: 4/2/2017 Move this into its own method
			if (realThrottle != calculatedThrottle) control.setThrottle(realThrottle = calculatedThrottle);
		}
		log("Maneuver complete!");
		control.setThrottle(0);
	}

	public double simpleBurnTime(SpaceCenter.Node node) throws IOException, RPCException, InterruptedException, StreamException {
		double force = vessel.getAvailableThrust();
		double isp = vessel.getSpecificImpulse() * 9.82;
		double m0 = vessel.getMass();
		double flowRate = force / isp;
		Stream<Double> remainingDeltaV = connection.addStream(node, "getRemainingDeltaV");
		double m1 = m0 / Math.exp(node.getRemainingDeltaV() / isp);
		double burnTime = (m0 - m1) / flowRate;
		return burnTime;
//		return new Recalculator<Double>() {
//			@Override
//			public Double get() throws IOException, RPCException, InterruptedException, StreamException {
//				return (m0 - (m0 / (Math.exp(remainingDeltaV.get() / isp)))) / flowRate;
//			}
//
//			@Override
//			public void remove() throws IOException, RPCException, InterruptedException, StreamException {
//				remainingDeltaV.remove();
//			}
//		};
	}

	//endregion
	//region Miscellaneous
	protected void sleep(double seconds) throws IOException, RPCException, InterruptedException, StreamException {
		sleepUntil(ut.get() + Double.max(seconds, 0));
	}

	protected void sleepUntil(double end) throws IOException, RPCException, InterruptedException, StreamException {
		while (ut.get() < end) Thread.sleep(10);
	}

	protected final void log(@NotNull String msg, @Nullable Object... args) {
		Utils.log("[%s] %s", getClass().getSimpleName(), Utils.fmt(msg, args));
	}

	protected final void logo(@NotNull Object obj) {
		log(obj.toString());
	}

	public interface Recalculator<T> {

		T get() throws IOException, RPCException, InterruptedException, StreamException;

		void remove() throws IOException, RPCException, InterruptedException, StreamException;

	}

	//endregion

}
