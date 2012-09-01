package r;

import r.errors.*;

public final class Utils {

    public static void nyi() {
        throw RError.getNYI(null);
    }

    public static void nyi(String reason) {
        throw RError.getNYI(reason);
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }

    /**
     * From a type point of view, this works ... however all call to this function will perform the cast AFTER which
     * sucks
     */
    public static <T> T cast(Object obj, Class<T> clazz) {
        return clazz.cast(obj);
    }

    public static String getProperty(String key, String dfltValue) {
        return System.getProperty(key, dfltValue);
    }

    public static boolean getProperty(String key, boolean dfltValue) {
        return Boolean.parseBoolean(getProperty(key, dfltValue ? "true" : "false"));
    }

    public static boolean check(final boolean assertion) {
        assert assertion;
        return true;
    }

    public static boolean check(final boolean assertion, final String reason) {
        assert assertion : reason;
        return true;
    }

    public static final boolean DEBUG = true;

    public static void debug(String msg) {
        if (DEBUG) {
            System.err.println(msg);
        }
    }
}
