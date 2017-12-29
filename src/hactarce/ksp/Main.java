package hactarce.ksp;

import krpc.client.RPCException;
import krpc.client.StreamException;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException, RPCException, InterruptedException, StreamException {
		ConnectionManager m = new ConnectionManager("Hactar");
//		new Gui(m).showUI();
//		new SimpleOrbiter(m).launchToOrbit();
//		new Subroutine(m).run();
//		new NodeTools(m).execNode();
		new NodeBuilder(m).circularizeAtPe();
		while (!m.control.getNodes().isEmpty()) new NodeTools(m).execNode(0.01);
	}

}
