package hactarce.ksp;

import hactarce.Utils;
import krpc.client.RPCException;
import krpc.client.Stream;
import krpc.client.StreamException;
import krpc.client.services.UI;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.io.IOException;

public class Gui extends Subroutine {

	public Gui(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	public void showUI() throws IOException, RPCException, InterruptedException, StreamException {
		UI.Canvas canvas = m.ui.getStockCanvas();
		Pair<Double, Double> screenSize = canvas.getRectTransform().getSize();
		UI.Panel panel = canvas.addPanel(true);
		UI.Button button = panel.addButton("Full Throttle", true);
		button.getRectTransform().setPosition(new Pair<>(0.0, 20.0));
		UI.Text text = panel.addText("Thrust: 0 kN", true);
		text.getRectTransform().setPosition(new Pair<>(0.0, -20.0));
		text.setColor(new Triplet<>(1.0, 1.0, 1.0));
		text.setSize(18);
		Stream<Boolean> buttonClicked = m.connection.addStream(button, "getClicked");
		while (true) {
			if (buttonClicked.get()) {
				setThrottle(1);
				button.setClicked(false);
			}
			text.setContent(Utils.fmt("Thrust: %.0f kN", (m.vessel.getThrust() / 1000)));
			Thread.sleep(50);
		}
	}

}
