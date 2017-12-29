package hactarce.ksp;

import krpc.client.RPCException;
import krpc.client.StreamException;
import krpc.client.services.SpaceCenter;

import java.io.IOException;

public class NodeBuilder extends Subroutine {

	public NodeBuilder(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	public SpaceCenter.Node circularizeAtAp() throws IOException, RPCException, StreamException {
		return circularizeAt(m.orbit.getTimeToApoapsis(), m.orbit.getApoapsis());
	}

	public SpaceCenter.Node circularizeAtPe() throws IOException, RPCException, StreamException {
		return circularizeAt(m.orbit.getTimeToPeriapsis(), m.orbit.getPeriapsis());
	}

	private SpaceCenter.Node circularizeAt(double time, double radius) throws IOException, RPCException, StreamException {
		return m.control.addNode(time + m.ut.get(),
				(float) (OrbitMath.getOrbitVelocity(m.orbit.getBody(), radius) - Math.sqrt(m.orbit.getBody().getGravitationalParameter() * (2 / radius - 1 / m.orbit.getSemiMajorAxis()))),
				0,
				0);
	}

}
