package hactarce.ksp;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

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

}
