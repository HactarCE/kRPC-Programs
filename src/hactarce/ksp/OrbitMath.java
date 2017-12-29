package hactarce.ksp;

import krpc.client.RPCException;
import krpc.client.services.SpaceCenter;

import java.io.IOException;

public final class OrbitMath {

	private OrbitMath() {
	}

	static double getOrbitVelocity(SpaceCenter.CelestialBody body, double radius) throws IOException, RPCException {
		return Math.sqrt(body.getGravitationalParameter() / radius);
	}

}
