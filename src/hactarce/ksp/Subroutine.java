package hactarce.ksp;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import krpc.client.Connection;
import krpc.client.RPCException;
import krpc.client.Stream;
import krpc.client.StreamException;
import krpc.client.services.KRPC;
import krpc.client.services.SpaceCenter;

import java.io.IOException;

public abstract class Subroutine {

	public Connection connection;
	protected KRPC krpc;
	protected SpaceCenter spaceCenter;
	protected SpaceCenter.Vessel vessel;
	protected SpaceCenter.AutoPilot auto;
	protected SpaceCenter.Control control;

	protected Stream<Double> ut;

	public Subroutine(Connection connection) throws IOException, RPCException, InterruptedException, StreamException {
		log("Initializing %s subroutine...", getClass().getSimpleName());
		this.connection = connection;
		krpc = KRPC.newInstance(connection);
		spaceCenter = SpaceCenter.newInstance(connection);
		vessel = spaceCenter.getActiveVessel();
		auto = vessel.getAutoPilot();
		control = vessel.getControl();
		ut = connection.addStream(SpaceCenter.class, "getUT");
	}

	public abstract void execute() throws IOException, RPCException, InterruptedException, StreamException;

	protected void sleep(double seconds) throws IOException, RPCException, InterruptedException, StreamException {
		double end = ut.get() + Double.max(seconds, 0);
		while (ut.get() < end) Thread.sleep(10);
	}

	protected final void log(@NotNull String msg, @Nullable Object... args) {
		Utils.log("[%s] %s", getClass().getSimpleName(), Utils.fmt(msg, args));
	}

	protected final void logo(@NotNull Object obj) {
		log(obj.toString());
	}

}
