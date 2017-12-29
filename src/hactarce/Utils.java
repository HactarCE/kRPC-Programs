package hactarce;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.javatuples.Triplet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

	public static final Locale LOCALE = Locale.US;

	private Utils() {
	}

	public static String fmt(@NotNull String format, @Nullable Object... args) {
		return String.format(LOCALE, format, args);
	}

	public static void log(@NotNull String format, @Nullable Object... args) {
		System.out.println(new SimpleDateFormat("[HH:mm:ss] ").format(new Date()) + fmt(format, args));
	}

	public static Triplet<Double, Double, Double> crossProduct(
			Triplet<Double, Double, Double> u, Triplet<Double, Double, Double> v) {
		return new Triplet<>(
				u.getValue1() * v.getValue2() - u.getValue2() * v.getValue1(),
				u.getValue2() * v.getValue0() - u.getValue0() * v.getValue2(),
				u.getValue0() * v.getValue1() - u.getValue1() * v.getValue0()
		);
	}

	public static double dotProduct(Triplet<Double, Double, Double> u,
							 Triplet<Double, Double, Double> v) {
		return u.getValue0() * v.getValue0() +
				u.getValue1() * v.getValue1() +
				u.getValue2() * v.getValue2();
	}

	public static double magnitude(Triplet<Double, Double, Double> v) {
		return Math.sqrt(dotProduct(v, v));
	}

	// Compute the angle between vector x and y
	public static double angleBetweenVectors(Triplet<Double, Double, Double> u,
									  Triplet<Double, Double, Double> v) {
		double dp = dotProduct(u, v);
		if (dp == 0) {
			return 0;
		}
		double um = magnitude(u);
		double vm = magnitude(v);
		return Math.acos(dp / (um * vm)) * (180f / Math.PI);
	}

}
