package r.gnur;


public class GNUR {

    // nmath
    public static native double rnorm(double mu, double sigma);
    public static native boolean rnorm(double[] res, int n, double mu, double sigma);
    public static native boolean rnormNonChecking(double[] res, int n, double mu, double sigma);
    public static native boolean rnormStd(double[] res, int n);

    public static native boolean runifStd(double[] res, int n);

    public static native void set_seed(int[] rngKind); // rngKind is an array of (at least) 3 elements: generator id, i1, i2
    public static native void get_seed(int[] rngKind);

    // appl
    public static native void fft_factor(int n, int[] maxf, int[] maxp); // FIXME: could also use intW or similar instead of int[]
    public static native int fft_work(double[] ab, int nseg, int n, int nspn, int isn, double[] work, int[] iwork);

    public static native void dqrdc2(double[] x, int ldx, int n, int p, double tol, int[] k, double[] qraux, int[] jpvt, double[] work);
    public static native void dqrcf(double[] x, int n, int k, double[] qraux, double[] y, int ny, double[] b, int[] info);

    static {
        System.loadLibrary("gnurglue");
    }

}
