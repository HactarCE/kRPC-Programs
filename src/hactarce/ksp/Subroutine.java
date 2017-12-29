package hactarce.ksp;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import hactarce.Utils;
import krpc.client.RPCException;
import krpc.client.Stream;
import krpc.client.StreamException;

import java.io.IOException;

public abstract class Subroutine {

	protected ConnectionManager m;

	protected Stream<Float> throttle;

	private Subroutine() {
		throw new RuntimeException("Do not use default constructor; use Subroutine(ConnectionManager connectionManager) instead");
	}

	protected Subroutine(ConnectionManager connectionManager) {
		m = connectionManager;
	}

	protected void sleep(double seconds) throws IOException, RPCException, InterruptedException, StreamException {
		sleepUntil(m.ut.get() + Double.max(seconds, 0));
	}

	protected void sleepUntil(double end) throws IOException, RPCException, InterruptedException, StreamException {
		while (m.ut.get() < end) Thread.sleep(10);
	}

	protected final void log(@NotNull String msg, @Nullable Object... args) {
		Utils.log("[%s] %s", getClass().getSimpleName(), Utils.fmt(msg, args));
	}

	protected final void warpTo(Double ut) throws IOException, RPCException, InterruptedException, StreamException {
		m.spaceCenter.warpTo(ut, 100000, 2);
	}

	protected final void setThrottle(float value) throws IOException, RPCException, InterruptedException, StreamException {
		if (throttle == null) throttle = m.connection.addStream(m.control, "getThrottle");
		if (throttle.get() != value) m.control.setThrottle(value);
	}

}
