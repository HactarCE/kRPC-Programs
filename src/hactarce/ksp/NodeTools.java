package hactarce.ksp;

import com.sun.javafx.util.Utils;
import krpc.client.RPCException;
import krpc.client.Stream;
import krpc.client.StreamException;
import krpc.client.services.SpaceCenter;
import org.javatuples.Triplet;

import java.io.IOException;
import java.util.ArrayList;

public class NodeTools extends Subroutine {

	SpaceCenter.Node node;

	private Streamable<Float> availableThrust;
	private Streamable<Float> isp;
	private Streamable<Float> mass;
	private Streamable<Double> remainingNodeDeltaV;
	private Streamable<Double> timeToNode;

	private ArrayList<Streamable> streamables = new ArrayList<>();

//	private Stream<Float> availableThrust;
//	private Stream<Float> isp;
//	private Stream<Float> mass;
//	private Stream<Double> remainingNodeDeltaV;
//	private Stream<Double> timeToNode;

	public NodeTools(ConnectionManager connectionManager) throws IOException, RPCException {
		this(connectionManager, connectionManager.control.getNodes().get(0));
	}

	public NodeTools(ConnectionManager connectionManager, SpaceCenter.Node node) {
		super(connectionManager);
		this.node = node;
		//region Streamables
		streamables.add(availableThrust = new Streamable<>(
				() -> m.connection.addStream(m.vessel, "getAvailableThrust"),
				m.vessel::getAvailableThrust
		));
		streamables.add(isp = new Streamable<>(
				() -> m.connection.addStream(m.vessel, "getSpecificImpulse"),
				m.vessel::getSpecificImpulse
		));
		streamables.add(mass = new Streamable<>(
				() -> m.connection.addStream(m.vessel, "getMass"),
				m.vessel::getMass
		));
		streamables.add(remainingNodeDeltaV = new Streamable<>(
				() -> m.connection.addStream(node, "getRemainingDeltaV"),
				node::getRemainingDeltaV
		));
		streamables.add(timeToNode = new Streamable<>(
				() -> m.connection.addStream(node, "getTimeTo"),
				node::getTimeTo
		));
		//endregion
	}

	public void useStreams(boolean useStreams) throws IOException, RPCException, InterruptedException, StreamException {
		if (useStreams) for (Streamable streamable : streamables) streamable.open();
		else for (Streamable streamable : streamables) streamable.close();
	}

	public void execNode() throws IOException, RPCException, InterruptedException, StreamException {
		execNode(0.001);
	}

	public void execNode(double deltaVPrecision) throws IOException, RPCException, InterruptedException, StreamException {
		execNode(deltaVPrecision, 3);
	}

	public void execNode(double deltaVPrecision, float errorToleranceDegrees) throws IOException, RPCException, InterruptedException, StreamException {
		ConnectionManager.AutoPilotState autoPilotState = m.saveAutoPilotState();
		SpaceCenter.ReferenceFrame oldReferenceFrame = m.auto.getReferenceFrame();
		SpaceCenter.ReferenceFrame nodeReferenceFrame = node.getReferenceFrame();
		m.auto.setReferenceFrame(nodeReferenceFrame);
		m.auto.setTargetDirection(node.remainingBurnVector(nodeReferenceFrame));
		m.engageAuto();
		m.auto.wait_();
		log("Warping to burn...");
		warpTo(burnStartTime() - 5);
		m.auto.setTargetDirection(node.remainingBurnVector(nodeReferenceFrame));
		useStreams(true);
		sleepUntil(burnStartTime());
		Stream<Triplet<Double, Double, Double>> remainingNodeBurnVector = m.connection.addStream(node, "remainingBurnVector", nodeReferenceFrame);
		Stream<Float> autoPilotError = m.connection.addStream(m.auto, "getError");
		log("Executing maneuver node...");
		while (remainingNodeDeltaV.get() > deltaVPrecision) {
			m.auto.setTargetDirection(remainingNodeBurnVector.get());
			setThrottle((float) Utils.clamp(0.0,
					mass.get() / availableThrust.get() // more acceleration = lower throttle
							* remainingNodeDeltaV.get() // more delta v remaining = higher throttle
							// off-target = lower throttle
							* Utils.clamp(0, 2 - Math.pow(autoPilotError.get(), 2) / errorToleranceDegrees, 1),
					1.0));
		}
		setThrottle(0);
		log("Completed maneuver with %.3f m/s remaining (%.5f%% error)",
				remainingNodeDeltaV.get(), 100 * remainingNodeDeltaV.get() / node.getDeltaV());
		node.remove();
		useStreams(false);
		remainingNodeBurnVector.remove();
		autoPilotError.remove();
		autoPilotState.restore();
	}

	public double getSimpleRemainingBurnTime() throws IOException, RPCException, InterruptedException, StreamException {
		float isp = this.isp.get();
		float m0 = mass == null ? m.vessel.getMass() : mass.get();
		double m1 = m0 / Math.exp(remainingNodeDeltaV.get() / isp);
		double flowRate = availableThrust.get() / isp;
		return (m0 - m1) / flowRate;
	}

	public double burnStartTime() throws IOException, RPCException, InterruptedException, StreamException {
		return m.ut.get() + timeToNode.get() - getSimpleRemainingBurnTime() / 2;
	}

}
