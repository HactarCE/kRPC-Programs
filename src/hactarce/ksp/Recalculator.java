package hactarce.ksp;

import krpc.client.RPCException;
import krpc.client.StreamException;

import java.io.IOException;

public interface Recalculator<T> {
	T get() throws IOException, RPCException, StreamException;
}
