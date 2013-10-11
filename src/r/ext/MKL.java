package r.ext;

import r.*;

public class MKL {

    public static boolean use(int vectorSize) {
        return vectorSize >= 50; // Intel recommends 40, but that is without JNI cost
    }

    // VML
    public static native void vdAdd(int n, double[] a, double[] b, double[] y);
    public static native void vdSub(int n, double[] a, double[] b, double[] y);
    public static native void vdMul(int n, double[] a, double[] b, double[] y);
    public static native void vdDiv(int n, double[] a, double[] b, double[] y);
    public static native void vdPow(int n, double[] a, double[] b, double[] y);

    public static native void vzAdd(int n, double[] a, double[] b, double[] y);
    public static native void vzSub(int n, double[] a, double[] b, double[] y);
    public static native void vzMul(int n, double[] a, double[] b, double[] y);
    public static native void vzDiv(int n, double[] a, double[] b, double[] y);
    public static native void vzPow(int n, double[] a, double[] b, double[] y);

    public static native void vdPowx(int n, double[] a, double b, double[] y);

    public static native void vdSqr(int n, double[] a, double[] y);
    public static native void vdAbs(int n, double[] a, double[] y);
    public static native void vdSqrt(int n, double[] a, double[] y);

    public static native void vzAbs(int n, double[] a, double[] y);
    // public static native void vzSqrt(int n, double[] a, double[] y);


    static {
        System.loadLibrary(RContext.MKL_LIBRARY_NAME);
    }

}
