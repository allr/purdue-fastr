package r.ext;

import r.*;

public class SystemLibs {

    // libc/libm
    // LICENSE: The methods call to the C library / system's POSIX math library.
    public static native double pow(double x, double y);
    public static native void pow(double[] x, double[] y, double[] res, int size);
    public static native void pow(double[] x, double y, double[] res, int size);
    public static native void pow(double x, double[] y, double[] res, int size);

    public static native boolean fmod(double[] x, double[] y, double[] res, int size);

    public static native double exp(double x);

    static {
        System.loadLibrary(RContext.SYSTEM_LIBS_LIBRARY_NAME);
    }

}
