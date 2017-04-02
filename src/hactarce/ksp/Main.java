package hactarce.ksp;

import krpc.client.Connection;
import krpc.client.RPCException;
import krpc.client.services.KRPC;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, RPCException {
		Connection connection = Connection.newInstance();
		KRPC krpc = KRPC.newInstance(connection);
		System.out.println("krpc.getStatus().getVersion() = " + krpc.getStatus().getVersion());
	}
}
