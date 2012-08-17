package r;

public final class Utils {
    public static void nyi() {
        throw new RuntimeException("Not yet implemented ...");
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) cast(obj);
     }
    /**
     * From a type point of view, this works ...
     * however all call to this function will perform the cast AFTER which sucks
     */
    public static <T> T cast(Object obj, Class<T> clazz) {
        return clazz.cast(obj);
     }

    public static String getProperty(String key, String dfltValue) {
        return System.getProperty(key, dfltValue);
    }
}
