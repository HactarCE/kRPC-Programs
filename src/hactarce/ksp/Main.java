package hactarce.ksp;

import krpc.client.Connection;
import krpc.client.RPCException;
import krpc.client.StreamException;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException, RPCException, InterruptedException, StreamException {
//		new Subroutine(Connection.newInstance()) {
//			@Override
//			public void execute() throws IOException, RPCException, InterruptedException, StreamException {
//				throw new RuntimeException();
//			}
//		}.executeNode();
		new SimpleOrbiter(Connection.newInstance()).execute();
	}

}
