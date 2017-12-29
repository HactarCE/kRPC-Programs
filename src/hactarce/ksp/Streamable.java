package hactarce.ksp;

import krpc.client.RPCException;
import krpc.client.Stream;
import krpc.client.StreamException;

import java.io.IOException;

public class Streamable<T> {

	private StreamAdder<T> streamAdder;
	private RemoteGetter<T> remoteGetter;
	private Stream<T> stream;

	public Streamable(StreamAdder<T> streamAdder, RemoteGetter<T> remoteGetter) {
		this.streamAdder = streamAdder;
		this.remoteGetter = remoteGetter;
	}

	public T get() throws IOException, RPCException, StreamException {
		return stream == null ? remoteGetter.get() : stream.get();
	}

	public boolean isStreamOpen() {
		return stream != null;
	}

	public Streamable<T> open() throws IOException, RPCException, StreamException {
		if (stream == null) stream = streamAdder.addStream();
		return this;
	}

	public Streamable<T> close() throws IOException, RPCException {
		if (stream != null) stream.remove();
		stream = null;
		return this;
	}

	public void useStream(boolean useStream) throws IOException, RPCException, StreamException {
		if (useStream) open();
		else close();
	}

	public interface StreamAdder<T> {
		Stream<T> addStream() throws IOException, RPCException, StreamException;
	}

	public interface RemoteGetter<T> {
		T get() throws IOException, RPCException, StreamException;
	}

}
