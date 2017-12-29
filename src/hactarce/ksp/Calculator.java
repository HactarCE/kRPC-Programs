package hactarce.ksp;

import krpc.client.RPCException;
import krpc.client.StreamException;

import java.io.IOException;

public interface Calculator<T> {
	T get() throws IOException, RPCException, StreamException;

	public static interface Input<I, T> {
		T get(I input) throws IOException, RPCException, StreamException;
	}

}
